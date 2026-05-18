package com.login.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.login.model.Candidate;
import com.login.model.InterviewFeedback;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PDF Generation Service - Generates PDF documents for candidate details
 * 
 * @author Bob
 */
@Service
public class PdfGenerationService {

    /**
     * Generate PDF with candidate details
     */
    public byte[] generateCandidatePdf(Candidate candidate) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            Paragraph title = new Paragraph("CANDIDATE INFORMATION")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Add timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            Paragraph dateTime = new Paragraph("Generated on: " + timestamp)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(dateTime);

            // Create table with candidate details
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth();

            // Add header
            addTableHeader(table, "Field", "Value");

            // Add candidate details
            addTableRow(table, "Candidate ID", String.valueOf(candidate.getId()));
            addTableRow(table, "Name", candidate.getName());
            addTableRow(table, "Email", candidate.getEmail());
            addTableRow(table, "Phone", candidate.getPhone() != null ? candidate.getPhone() : "N/A");
            addTableRow(table, "Location", candidate.getLocation() != null ? candidate.getLocation() : "N/A");
            addTableRow(table, "Current CTC", candidate.getCurrentCtc() != null ? 
                    "₹ " + candidate.getCurrentCtc().toString() : "N/A");
            addTableRow(table, "Position", candidate.getPosition() != null ? candidate.getPosition() : "N/A");
            addTableRow(table, "Experience (Years)", candidate.getExperienceYears() != null ? 
                    String.valueOf(candidate.getExperienceYears()) : "N/A");
            addTableRow(table, "Skills", candidate.getSkills() != null ? candidate.getSkills() : "N/A");
            addTableRow(table, "Status", candidate.getStatus() != null ? candidate.getStatus() : "N/A");
            addTableRow(table, "Employment Type", candidate.getEmploymentType() != null ? 
                    candidate.getEmploymentType() : "N/A");
            
            if (candidate.getJoiningDate() != null) {
                addTableRow(table, "Joining Date", candidate.getJoiningDate().toString());
            }
            
            if (candidate.getOldCtc() != null) {
                addTableRow(table, "Old CTC", "₹ " + candidate.getOldCtc().toString());
            }
            
            if (candidate.getNewCtc() != null) {
                addTableRow(table, "New CTC", "₹ " + candidate.getNewCtc().toString());
            }

            document.add(table);

            // Add footer
            Paragraph footer = new Paragraph("\n\nThis is an auto-generated document.")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);

            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Add table header
     */
    private void addTableHeader(Table table, String header1, String header2) {
        Cell cell1 = new Cell()
                .add(new Paragraph(header1).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        
        Cell cell2 = new Cell()
                .add(new Paragraph(header2).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        
        table.addHeaderCell(cell1);
        table.addHeaderCell(cell2);
    }

    /**
     * Add table row
     */
    private void addTableRow(Table table, String field, String value) {
        Cell cell1 = new Cell()
                .add(new Paragraph(field).setBold())
                .setPadding(5);
        
        Cell cell2 = new Cell()
                .add(new Paragraph(value))
                .setPadding(5);
        
        table.addCell(cell1);
        table.addCell(cell2);
    }

    /**
     * Generate Technical Interview Assessment Form PDF
     */
    public byte[] generateInterviewFeedbackPdf(InterviewFeedback feedback) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            Paragraph title = new Paragraph("Technical Interview Assessment Form - AWS Cloud Full Stack")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Add timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            Paragraph dateTime = new Paragraph("Generated on: " + timestamp)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(15);
            document.add(dateTime);

            // Basic Information Section
            addSectionHeader(document, "BASIC INFORMATION");
            Table basicTable = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
            addFeedbackRow(basicTable, "Candidate Name", feedback.getCandidateName());
            addFeedbackRow(basicTable, "Source", feedback.getSource());
            addFeedbackRow(basicTable, "Years of Experience", String.valueOf(feedback.getYearsOfExperience()));
            addFeedbackRow(basicTable, "Years of Experience in Tech", String.valueOf(feedback.getYearsOfExperienceInTech()));
            addFeedbackRow(basicTable, "Evaluation Type", feedback.getEvaluationType());
            addFeedbackRow(basicTable, "Evaluator Names & ID", feedback.getEvaluatorNames());
            addFeedbackRow(basicTable, "Evaluation Date", feedback.getEvaluationDate().toString());
            addFeedbackRow(basicTable, "Job Role Specification (JRS)", feedback.getJobRoleSpecification());
            addFeedbackRow(basicTable, "Job Description (JD)", feedback.getJobDescription() != null ? feedback.getJobDescription() : "N/A");
            addFeedbackRow(basicTable, "Account Name / Hire Ahead", feedback.getAccountName());
            addFeedbackRow(basicTable, "Job Level", feedback.getJobLevel());
            document.add(basicTable);
            document.add(new Paragraph("\n"));

            // Soft Skills Section
            addSectionHeader(document, "A. SOFT SKILLS");
            Table softSkillsTable = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20})).useAllAvailableWidth();
            addRatingHeader(softSkillsTable, "Skill", "Rating", "Type");
            addRatingRow(softSkillsTable, "Communication - grammar, correctness, pronunciation, clarity",
                        formatRating(feedback.getCommunicationRating()), "M");
            document.add(softSkillsTable);
            document.add(new Paragraph("\n"));

            // Technical Skills Section
            addSectionHeader(document, "B. TECHNICAL SKILLS");
            Table techTable = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20})).useAllAvailableWidth();
            addRatingHeader(techTable, "Skill", "Rating", "Type");
            addRatingRow(techTable, "AWS Native Services", formatRating(feedback.getAwsNativeServicesRating()), "M");
            addRatingRow(techTable, "AWS Integration Services", formatRating(feedback.getAwsIntegrationServicesRating()), "M");
            addRatingRow(techTable, "AWS Compute Services", formatRating(feedback.getAwsComputeServicesRating()), "M");
            addRatingRow(techTable, ".NET / Java / Python / Node.JS", formatRating(feedback.getProgrammingLanguageRating()), "M");
            addRatingRow(techTable, "AWS DevOps Services", formatRating(feedback.getAwsDevOpsServicesRating()), "M");
            addRatingRow(techTable, "AWS Storage (RDS/Aurora/DynamoDB/S3)", formatRating(feedback.getAwsStorageRating()), "M");
            addRatingRow(techTable, "Agile/Scrum", formatRating(feedback.getAgileScrumRating()), "M");
            addRatingRow(techTable, "AWS CLI", formatRating(feedback.getAwsCliRating()), "M");
            addRatingRow(techTable, "Software Deployment/Configuration/Release", formatRating(feedback.getDeploymentManagementRating()), "M");
            addRatingRow(techTable, "AWS EKS/ECS or Kubernetes", formatRating(feedback.getContainerOrchestrationRating()), "M");
            addRatingRow(techTable, "Microservices Design Patterns", formatRating(feedback.getMicroservicesDesignPatternsRating()), "M");
            addRatingRow(techTable, "Microservices Communications", formatRating(feedback.getMicroservicesCommunicationRating()), "M");
            addRatingRow(techTable, "AutoScaling, Disaster Recovery, Backup", formatRating(feedback.getDisasterRecoveryRating()), "M");
            addRatingRow(techTable, "Container and Containerization (Docker/Podman)", formatRating(feedback.getContainerizationRating()), "M");
            addRatingRow(techTable, "IaaC (Terraform / CloudFormation)", formatRating(feedback.getIacRating()), "M");
            addRatingRow(techTable, "FrontEnd Stack: Angular/React/Node/etc", formatRating(feedback.getFrontendStackRating()), "M");
            addRatingRow(techTable, "FrontEnd Stack: HTML/CSS, Responsive design", formatRating(feedback.getHtmlCssRating()), "M");
            addRatingRow(techTable, "SpringCloud for AWS", formatRating(feedback.getSpringCloudAwsRating()), "D");
            addRatingRow(techTable, "SQL tuning and DB operations", formatRating(feedback.getSqlTuningRating()), "D");
            addRatingRow(techTable, "Setup, Packaging and Deployment", formatRating(feedback.getSetupPackagingRating()), "D");
            document.add(techTable);
            document.add(new Paragraph("\n"));

            // Panel Notes
            if (feedback.getAwsNativeServicesNotes() != null && !feedback.getAwsNativeServicesNotes().isEmpty()) {
                addSectionHeader(document, "PANEL NOTES");
                Paragraph notes = new Paragraph(feedback.getAwsNativeServicesNotes())
                        .setFontSize(10)
                        .setMarginBottom(10);
                document.add(notes);
            }

            // Certifications
            addSectionHeader(document, "C. CERTIFICATIONS");
            Paragraph certs = new Paragraph(feedback.getCertifications() != null ? feedback.getCertifications() : "None")
                    .setFontSize(10)
                    .setMarginBottom(10);
            document.add(certs);

            // Architecting and Solutioning (if applicable)
            if (feedback.getJobLevel().equals("TL") || feedback.getJobLevel().equals("ML") || feedback.getJobLevel().equals("Architect")) {
                addSectionHeader(document, "D. ARCHITECTING AND SOLUTIONING");
                Table archTable = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20})).useAllAvailableWidth();
                addRatingHeader(archTable, "Skill", "Rating", "Type");
                addRatingRow(archTable, "Functional point / WBS estimation", formatRating(feedback.getEstimationRating()), "M");
                addRatingRow(archTable, "Architecture (HLD, LLD, etc.)", formatRating(feedback.getArchitectureRating()), "M");
                addRatingRow(archTable, "Solutioning / RFP Support", formatRating(feedback.getSolutioningRating()), "M");
                addRatingRow(archTable, "Delivery - Methodologies, Processes, Quality", formatRating(feedback.getDeliveryMethodologiesRating()), "D");
                addRatingRow(archTable, "Operations - Infrastructure, Support, Recruitment", formatRating(feedback.getOperationsRating()), "D");
                addRatingRow(archTable, "Customer Handling - Communication, Account Mgmt", formatRating(feedback.getCustomerHandlingRating()), "D");
                addRatingRow(archTable, "Other Management Skills", formatRating(feedback.getOtherManagementSkillsRating()), "D");
                document.add(archTable);
                document.add(new Paragraph("\n"));
            }

            // Overall Rating and Recommendations
            addSectionHeader(document, "E. OVERALL RATING");
            Table overallTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            addFeedbackRow(overallTable, "Overall Rating (Mandatory Skills)", String.valueOf(feedback.getOverallRating()));
            addFeedbackRow(overallTable, "Tool Recommendation", feedback.getToolRecommendation());
            addFeedbackRow(overallTable, "Tech Panel Recommendation", feedback.getTechPanelRecommendation());
            document.add(overallTable);
            document.add(new Paragraph("\n"));

            // Feedback Section
            addSectionHeader(document, "FEEDBACK");
            Table feedbackTable = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
            
            Cell feedbackLabelCell = new Cell().add(new Paragraph("Overall feedback on this candidate (Mandatory)").setBold())
                    .setBackgroundColor(new DeviceRgb(240, 240, 240)).setPadding(5);
            feedbackTable.addCell(feedbackLabelCell);
            
            Cell feedbackValueCell = new Cell().add(new Paragraph(feedback.getOverallFeedback()))
                    .setPadding(8);
            feedbackTable.addCell(feedbackValueCell);
            
            Cell suitabilityLabelCell = new Cell().add(new Paragraph("How suitable for this requirement").setBold())
                    .setBackgroundColor(new DeviceRgb(240, 240, 240)).setPadding(5);
            feedbackTable.addCell(suitabilityLabelCell);
            
            Cell suitabilityValueCell = new Cell().add(new Paragraph(feedback.getSuitabilityForRequirement() != null ?
                    feedback.getSuitabilityForRequirement() : "N/A"))
                    .setPadding(8);
            feedbackTable.addCell(suitabilityValueCell);
            
            Cell improvementLabelCell = new Cell().add(new Paragraph("Improvement / Focus area").setBold())
                    .setBackgroundColor(new DeviceRgb(240, 240, 240)).setPadding(5);
            feedbackTable.addCell(improvementLabelCell);
            
            Cell improvementValueCell = new Cell().add(new Paragraph(feedback.getImprovementFocusArea() != null ?
                    feedback.getImprovementFocusArea() : "N/A"))
                    .setPadding(8);
            feedbackTable.addCell(improvementValueCell);
            
            document.add(feedbackTable);
            document.add(new Paragraph("\n"));

            // Declaration
            addSectionHeader(document, "DECLARATION");
            Paragraph declaration = new Paragraph(
                "I confirm:\n" +
                "i) The candidate I interviewed/will interview is not referred by me or otherwise known to me in any manner.\n" +
                "ii) I have not received/or am not entitled to seek or receive, any benefit, monetary or otherwise, as a result of this hiring decision of mine, whether directly or indirectly, and whether for myself or for anyone else.\n" +
                "iii) If I know any candidate in any manner, whether personally or professionally, or indirectly through a reference or otherwise, I shall immediately bring this to the attention of my manager for their advice. I shall not participate in the recruitment process unless specifically authorized by my manager and/or IBM Recruitment.\n" +
                "iv) I understand that if the above is found to be untrue or misleading, this will be viewed as a BCG violation, and IBM may initiate disciplinary action against me."
            ).setFontSize(9).setMarginBottom(15);
            document.add(declaration);

            // Signature
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
            addFeedbackRow(signatureTable, "Signature of the evaluator(s)", feedback.getEvaluatorSignature());
            document.add(signatureTable);

            // Footer
            Paragraph footer = new Paragraph("\n\nThis is an auto-generated Technical Interview Assessment Form.")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

            // Close document
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Interview Feedback PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Add section header
     */
    private void addSectionHeader(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setFontSize(12)
                .setBold()
                .setBackgroundColor(new DeviceRgb(200, 200, 200))
                .setPadding(5)
                .setMarginTop(5)
                .setMarginBottom(5);
        document.add(header);
    }

    /**
     * Add feedback row to table
     */
    private void addFeedbackRow(Table table, String field, String value) {
        Cell cell1 = new Cell()
                .add(new Paragraph(field).setBold().setFontSize(9))
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(5);
        
        Cell cell2 = new Cell()
                .add(new Paragraph(value != null ? value : "N/A").setFontSize(9))
                .setPadding(5);
        
        table.addCell(cell1);
        table.addCell(cell2);
    }

    /**
     * Add rating header
     */
    private void addRatingHeader(Table table, String col1, String col2, String col3) {
        Cell cell1 = new Cell()
                .add(new Paragraph(col1).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        Cell cell2 = new Cell()
                .add(new Paragraph(col2).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        Cell cell3 = new Cell()
                .add(new Paragraph(col3).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        table.addHeaderCell(cell1);
        table.addHeaderCell(cell2);
        table.addHeaderCell(cell3);
    }

    /**
     * Add rating row
     */
    private void addRatingRow(Table table, String skill, String rating, String type) {
        Cell cell1 = new Cell()
                .add(new Paragraph(skill).setFontSize(9))
                .setPadding(5);
        
        Cell cell2 = new Cell()
                .add(new Paragraph(rating).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        Cell cell3 = new Cell()
                .add(new Paragraph(type).setFontSize(9))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
    }

    /**
     * Format rating value
     */
    private String formatRating(Double rating) {
        if (rating == null) {
            return "N/A";
        }
        return String.valueOf(rating);
    }
}

// Made with Bob