# Interview Position to JRS Migration Guide

## Overview
This guide documents the migration from using "Position" to "JRS" (Job Requisition System) in the Interview entity and the removal of the "Notes" field from the Interviews tab in the HR Dashboard.

## Changes Made

### 1. Backend Changes

#### Database Model Changes
- **Interview.java**: 
  - Renamed `position` field to `jrs`
  - Removed `notes` field
  - Updated constructor to remove `notes` parameter
  - Updated getters and setters

#### DTO Changes
- **CandidateInterviewDTO.java**:
  - Renamed `position` field to `jrs`
  - Removed `notes` field
  - Updated constructor and getters/setters

#### Service Layer Changes
- **InterviewService.java**:
  - Updated `scheduleInterview()` methods to use `jrs` instead of `position`
  - Removed `notes` parameter
  - Updated `updateInterview()` method
  - Updated DTO mapping to use `jrs`

- **HRService.java**:
  - Updated interview scheduling to use `candidate.getJrs()` instead of `candidate.getPosition()`
  - Removed notes handling
  - Updated response mapping

- **InterviewFeedbackService.java**:
  - Updated to use `interview.getJrs()` instead of `interview.getPosition()`

- **EmailService.java**:
  - Updated all email methods to use `jrs` parameter instead of `position`
  - Updated email templates to reference JRS

#### Controller Changes
- **InterviewController.java**:
  - Updated to accept `jrs` instead of `position` in request body
  - Removed `notes` parameter handling

### 2. Frontend Changes

#### HRDashboard.js
- **Interviews Tab Table**:
  - Changed column header from "Position" to "JRS"
  - Updated data binding from `interview.position` to `interview.jrs`
  - Removed "Notes" column completely
  - Removed notes display logic

### 3. Database Migration

A migration script has been created at:
`backend/src/main/resources/sql/migrate_interview_position_to_jrs.sql`

#### Migration Steps:

1. **Backup your database** before running any migration!

2. **For MySQL/MariaDB**, run:
   ```sql
   ALTER TABLE interviews CHANGE COLUMN position jrs VARCHAR(255) NOT NULL;
   ALTER TABLE interviews DROP COLUMN notes;
   ```

3. **For PostgreSQL**, run:
   ```sql
   ALTER TABLE interviews RENAME COLUMN position TO jrs;
   ALTER TABLE interviews DROP COLUMN notes;
   ```

4. **For H2 Database (Development)**, run:
   ```sql
   ALTER TABLE interviews ALTER COLUMN position RENAME TO jrs;
   ALTER TABLE interviews DROP COLUMN notes;
   ```

## Testing Checklist

### Backend Testing
- [ ] Verify Interview entity saves with `jrs` field
- [ ] Test interview scheduling through HR Dashboard
- [ ] Verify interview data retrieval shows `jrs` instead of `position`
- [ ] Test email notifications contain JRS information
- [ ] Verify interview feedback submission works correctly

### Frontend Testing
- [ ] Open HR Dashboard and navigate to Interviews tab
- [ ] Verify "JRS" column header is displayed
- [ ] Verify "Notes" column is removed
- [ ] Check that JRS values are displayed correctly for all interviews
- [ ] Test interview scheduling form (if it references position/jrs)
- [ ] Verify no console errors related to position or notes fields

### Integration Testing
- [ ] Schedule a new interview and verify JRS is saved
- [ ] View scheduled interviews in the Interviews tab
- [ ] Verify email notifications are sent with JRS information
- [ ] Test interview feedback flow with JRS field

## Rollback Plan

If you need to rollback these changes:

1. **Database Rollback**:
   ```sql
   -- MySQL/MariaDB
   ALTER TABLE interviews CHANGE COLUMN jrs position VARCHAR(255) NOT NULL;
   ALTER TABLE interviews ADD COLUMN notes VARCHAR(1000);
   
   -- PostgreSQL
   ALTER TABLE interviews RENAME COLUMN jrs TO position;
   ALTER TABLE interviews ADD COLUMN notes VARCHAR(1000);
   ```

2. **Code Rollback**: Revert the Git commits or restore from backup

## Notes

- The Candidate model already has a `jrs` field, so this change aligns the Interview entity with the Candidate entity
- The `position` field in the Candidate model is still retained for backward compatibility
- Email templates now reference "JRS" instead of "Position"
- No changes were made to the Candidate entity's `position` field

## Related Files

### Backend Files Modified:
- `backend/src/main/java/com/login/model/Interview.java`
- `backend/src/main/java/com/login/dto/CandidateInterviewDTO.java`
- `backend/src/main/java/com/login/service/InterviewService.java`
- `backend/src/main/java/com/login/service/HRService.java`
- `backend/src/main/java/com/login/service/InterviewFeedbackService.java`
- `backend/src/main/java/com/login/service/EmailService.java`
- `backend/src/main/java/com/login/controller/InterviewController.java`

### Frontend Files Modified:
- `frontend/src/components/HRDashboard.js`

### New Files Created:
- `backend/src/main/resources/sql/migrate_interview_position_to_jrs.sql`
- `INTERVIEW_JRS_MIGRATION_GUIDE.md`

## Support

If you encounter any issues after this migration, please:
1. Check the console logs for errors
2. Verify the database migration was successful
3. Ensure all backend services are restarted
4. Clear browser cache and reload the frontend

---
**Migration Date**: 2026-05-20  
**Author**: Bob