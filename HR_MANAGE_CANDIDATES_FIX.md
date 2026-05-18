# HR Manage Candidates - Delete Candidate Fix

## Issue Description
When attempting to delete a candidate from the HR Dashboard, the operation failed with a foreign key constraint error:

```
❌ could not execute batch [Cannot delete or update a parent row: a foreign key constraint fails 
(`logindb`.`password_history`, CONSTRAINT `FK5pj9ewu59pb3s05n3e9ccybt1` FOREIGN KEY (`user_id`) 
REFERENCES `users` (`id`))] [delete from users where id=?]; SQL [delete from users where id=?]; 
constraint [null]
```

## Root Cause
The `password_history` table has a foreign key constraint referencing the `users` table. When deleting a candidate:
1. The candidate record was deleted first
2. Then the associated user account was deleted
3. However, the password history records for that user were not deleted first
4. This caused a foreign key constraint violation

## Solution Implemented

### Changes Made to `HRService.java`

1. **Added Import Statements**:
   - Added `PasswordHistory` model import
   - Added `PasswordHistoryRepository` import

2. **Added Repository Dependency**:
   ```java
   @Autowired
   private PasswordHistoryRepository passwordHistoryRepository;
   ```

3. **Updated `deleteCandidate()` Method**:
   - Modified the deletion sequence to handle cascading deletes properly
   - Added password history cleanup before user deletion
   - Added flush operations to ensure database consistency

### Deletion Sequence (Fixed)

The corrected deletion order is now:

1. **Verify HR permissions** - Ensure the HR owns the candidate
2. **Delete candidate record** - Remove from `candidates` table
3. **Delete password history** - Remove all password history records for the user
4. **Delete user account** - Finally delete the user from `users` table

### Code Changes

```java
@Transactional
public void deleteCandidate(Long hrId, Long candidateId) {
    // ... verification code ...
    
    // Delete the candidate record first
    candidateRepository.delete(candidate);
    candidateRepository.flush();
    
    // Handle user account deletion with password history cleanup
    if (userOpt.isPresent()) {
        User user = userOpt.get();
        if ("CANDIDATE".equals(user.getRole())) {
            // Delete password history records first
            List<PasswordHistory> passwordHistories = 
                passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user);
            if (!passwordHistories.isEmpty()) {
                passwordHistoryRepository.deleteAll(passwordHistories);
                passwordHistoryRepository.flush();
            }
            
            // Now safe to delete the user
            userRepository.delete(user);
            userRepository.flush();
        }
    }
}
```

## Key Improvements

1. **Proper Cascade Handling**: Deletes related records in the correct order
2. **Transaction Safety**: Uses `@Transactional` to ensure atomicity
3. **Database Consistency**: Uses `flush()` to ensure changes are committed immediately
4. **Better Logging**: Added detailed console logs for debugging
5. **Role Verification**: Only deletes users with CANDIDATE role for safety

## Testing

To test the fix:

1. **Create a candidate** through HR Dashboard
2. **Verify candidate appears** in the Manage Candidates section
3. **Delete the candidate** using the delete button
4. **Verify deletion succeeds** without foreign key errors
5. **Check database** to ensure all related records are removed:
   - Candidate record deleted from `candidates` table
   - Password history deleted from `password_history` table
   - User account deleted from `users` table

## Database Tables Affected

- `candidates` - Candidate profile data
- `password_history` - Password history records
- `users` - User authentication data

## Foreign Key Relationships

```
users (id) <--- password_history (user_id)
users (id) <--- candidates (assigned_panelist_id)
users (id) <--- candidates (hr_id)
```

## Benefits

✅ **No more foreign key constraint errors**
✅ **Complete data cleanup** - All related records are properly deleted
✅ **Transaction safety** - All-or-nothing deletion
✅ **Better error handling** - Clear logging for debugging
✅ **Data integrity** - Maintains referential integrity

## Files Modified

- `backend/src/main/java/com/login/service/HRService.java`

## Related Features

- HR Dashboard Candidate Management
- User Account Management
- Password History Tracking
- Candidate Lifecycle Management

---

**Status**: ✅ Fixed
**Date**: 2026-05-16
**Author**: Bob