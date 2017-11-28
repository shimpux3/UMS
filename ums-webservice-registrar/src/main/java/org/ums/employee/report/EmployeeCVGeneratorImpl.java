package org.ums.employee.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.ums.employee.academic.AcademicInformation;
import org.ums.employee.academic.AcademicInformationManager;
import org.ums.employee.additional.AdditionalInformationManager;
import org.ums.employee.award.AwardInformation;
import org.ums.employee.award.AwardInformationManager;
import org.ums.employee.experience.ExperienceInformation;
import org.ums.employee.experience.ExperienceInformationManager;
import org.ums.employee.personal.PersistentPersonalInformation;
import org.ums.employee.personal.PersonalInformation;
import org.ums.employee.personal.PersonalInformationManager;
import org.ums.employee.publication.PublicationInformation;
import org.ums.employee.publication.PublicationInformationManager;
import org.ums.employee.service.ServiceInformation;
import org.ums.employee.service.ServiceInformationManager;
import org.ums.employee.training.TrainingInformation;
import org.ums.employee.training.TrainingInformationManager;
import org.ums.enums.common.BloodGroupType;
import org.ums.enums.common.MaritalStatusType;
import org.ums.enums.common.NationalityType;
import org.ums.enums.common.ReligionType;
import org.ums.enums.registrar.TrainingCategory;
import org.ums.manager.common.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeCVGeneratorImpl implements EmployeeCVGenerator {

  @Autowired
  PersonalInformationManager mPersonalInformationManager;

  @Autowired
  AcademicInformationManager mAcademicInformationManager;

  @Autowired
  PublicationInformationManager mPublicationInformationManager;

  @Autowired
  TrainingInformationManager mTrainingInformationManager;

  @Autowired
  AwardInformationManager mAwardInformationManager;

  @Autowired
  ExperienceInformationManager mExperienceInformationManager;

  @Autowired
  AdditionalInformationManager mAdditionalInformationManager;

  @Autowired
  ServiceInformationManager mServiceInformationManager;

  @Autowired
  CountryManager mCountryManager;

  @Autowired
  DivisionManager mDIvisionManager;

  @Autowired
  DistrictManager mDistrictManager;

  @Autowired
  ThanaManager mThanaManager;

  @Autowired
  AcademicDegreeManager mAcademicDegreeManager;

  @Override
  public void createEmployeeCV(String pEmployeeId, OutputStream pOutputStream) throws IOException, DocumentException {

    PersonalInformation personalInformation = new PersistentPersonalInformation();
    try {
      personalInformation = mPersonalInformationManager.getPersonalInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {
    }

    List<AcademicInformation> academicInformation = new ArrayList<>();
    try {
      academicInformation = mAcademicInformationManager.getEmployeeAcademicInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }

    List<PublicationInformation> publicationInformation = new ArrayList<>();
    try {
      publicationInformation = mPublicationInformationManager.getEmployeePublicationInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }

    List<TrainingInformation> trainingInformation = new ArrayList<>();
    try {
      trainingInformation = mTrainingInformationManager.getEmployeeTrainingInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }

    List<AwardInformation> awardInformation = new ArrayList<>();
    try {
      awardInformation = mAwardInformationManager.getEmployeeAwardInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }
    List<ExperienceInformation> experienceInformation = new ArrayList<>();
    try {
      experienceInformation = mExperienceInformationManager.getEmployeeExperienceInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }

    List<ServiceInformation> serviceInformation = new ArrayList<>();
    try {
      serviceInformation = mServiceInformationManager.getServiceInformation(pEmployeeId);
    } catch(EmptyResultDataAccessException e) {

    }

    Document document = new Document();
    document.addTitle("Curriculum Vitae");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfWriter writer = PdfWriter.getInstance(document, baos);

    Font generalFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
    Font generalBoldFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 11);
    Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 12);
    Font titleSmallFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 11);
    Font italicFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 11);
    Font italicSmallFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 9);

    document.open();
    document.setPageSize(PageSize.A4);

    // ------------------------------------------------------ Introduction
    // --------------------------------------------------------------

    String imageUrl = "https://lh4.googleusercontent.com/-KFRA5hmh56w/AAAAAAAAAAI/AAAAAAAAAEw/GONhyI4cuy8/photo.jpg";

    Image image = Image.getInstance(new URL(imageUrl));
    image.setAlignment(2);
    image.scaleAbsolute(100f, 110f);

    Chunk chunk = new Chunk();
    chunk.setFont(titleFont);
    chunk.append("Curriculum Vitae");

    Paragraph paragraph = new Paragraph();
    paragraph.setAlignment(Element.ALIGN_CENTER);
    paragraph.add(chunk);
    emptyLine(paragraph, 1);
    document.add(paragraph);

    chunk = new Chunk();
    chunk.setFont(italicFont);
    chunk.append("of");

    paragraph = new Paragraph();
    paragraph.setAlignment(Element.ALIGN_CENTER);
    paragraph.add(chunk);
    emptyLine(paragraph, 1);
    document.add(paragraph);

    PdfPTable innerTable = new PdfPTable(new float[] {new Float(1.5), new Float(7)});
    innerTable.setWidthPercentage(100);
    innerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    String fName = personalInformation.getFirstName() == null ? "" : personalInformation.getFirstName();
    String lName = personalInformation.getLastName() == null ? "" : personalInformation.getLastName();
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), "Name:", generalFont));
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), fName + " " + lName, generalFont));
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), "Email: ", generalFont));
    String personalEmail = personalInformation.getPersonalEmail() == null ? "" : personalInformation.getPersonalEmail();
    String orgEmail =
        personalInformation.getOrganizationalEmail() == null ? "" : ", " + personalInformation.getOrganizationalEmail();
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), personalEmail + orgEmail, generalFont));
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), "Phone:", generalFont));
    String mobile = personalInformation.getMobileNumber() == null ? "" : personalInformation.getMobileNumber();
    String phone = personalInformation.getPhoneNumber() == null ? "" : ", " + personalInformation.getPhoneNumber();
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), mobile + phone, generalFont));
    String addressLine1 =
        personalInformation.getPresentAddressLine1() == null ? "" : personalInformation.getPresentAddressLine1();
    String addressLine2 =
        personalInformation.getPresentAddressLine2() == null ? "" : ", " + personalInformation.getPresentAddressLine2()
            + " ";
    String addressDistrict =
        personalInformation.getPresentAddressDistrictId() == null
            || personalInformation.getPresentAddressDistrictId() == 0 ? "" : mDistrictManager.get(
            personalInformation.getPresentAddressDistrictId()).getDistrictName()
            + " ";
    String addressThana =
        personalInformation.getPresentAddressThanaId() == null || personalInformation.getPresentAddressThanaId() == 0 ? ""
            : mThanaManager.get(personalInformation.getPresentAddressThanaId()).getThanaName() + " ";
    String postCode =
        personalInformation.getPresentAddressPostCode() == null ? "" : " - "
            + personalInformation.getPresentAddressPostCode() + " ";
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), "Address:", generalFont));
    innerTable.addCell(prepareCell(new Chunk(), new PdfPCell(), addressLine1 + addressLine2 + addressThana
        + addressDistrict + postCode, generalFont));

    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    PdfPCell cell = new PdfPCell();
    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(innerTable);
    table.addCell(cell);

    cell = new PdfPCell();
    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(image);
    table.addCell(cell);
    document.add(table);

    // ---------------------------------------------- personal information
    // ----------------------------------------

    prepareSectionHeader(new PdfPTable(1), new Chunk(), new PdfPCell(), new Paragraph(), document, titleSmallFont,
        "Basic");

    table = new PdfPTable(new float[] {new Float(1.5), new Float(7)});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    // String fName = personalInformation.getFirstName() == null ? "" :
    // personalInformation.getFirstName();
    // String lName = personalInformation.getLastName() == null ? "" :
    // personalInformation.getLastName();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Name:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), fName + " " + lName, generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Father's Name:", generalFont));
    String fatherName = personalInformation.getFatherName() == null ? "" : personalInformation.getFatherName();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), fatherName, generalFont));
    String motherName = personalInformation.getMotherName() == null ? "" : personalInformation.getMotherName();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Mother's Name:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), motherName, generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Gender:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), personalInformation.getGender() == null ? ""
        : personalInformation.getGender().equals("M") ? "Male" : "Female", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Blood Group:", generalFont));
    String bloodGroup =
        personalInformation.getBloodGroupId() == null || personalInformation.getBloodGroupId() == 0 ? ""
            : BloodGroupType.get(personalInformation.getBloodGroupId()).getLabel();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), bloodGroup, generalFont));
    String nationality =
        personalInformation.getNationalityId() == null || personalInformation.getNationalityId() == 0 ? ""
            : NationalityType.get(personalInformation.getNationalityId()).getLabel();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Nationality:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), nationality, generalFont));
    String religion =
        personalInformation.getReligionId() == null || personalInformation.getReligionId() == 0 ? "" : ReligionType
            .get(personalInformation.getReligionId()).getLabel();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Religion:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), religion, generalFont));
    String marital_status =
        personalInformation.getMaritalStatusId() == null || personalInformation.getMaritalStatusId() == 0 ? ""
            : MaritalStatusType.get(personalInformation.getMaritalStatusId()).getLabel();
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Marital Status:", generalFont));
    table.addCell(prepareCell(new Chunk(), new PdfPCell(), marital_status, generalFont));
    if(personalInformation.getMaritalStatusId() != null && personalInformation.getMaritalStatusId() != 1) // 2
                                                                                                          // is
                                                                                                          // for
                                                                                                          // married
    {
      table.addCell(prepareCell(new Chunk(), new PdfPCell(), "Spouse Name: ", generalFont));
      table.addCell(prepareCell(new Chunk(), new PdfPCell(), personalInformation.getSpouseName(), generalFont));
    }

    document.add(table);

    // ------------------------------------------------------ End
    // -------------------------------------------------------------------------------

    // ------------------------------------------------------ academic information
    // --------------------------------------------------------------

    prepareSectionHeader(new PdfPTable(1), new Chunk(), new PdfPCell(), new Paragraph(), document, titleSmallFont,
        "Education");

    table = new PdfPTable(new float[] {new Float(1.4), new Float(5), new Float(1.2), new Float(0.8)});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    chunk = new Chunk();
    chunk.setFont(generalBoldFont);
    chunk.append("Degree");

    cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(chunk);
    table.addCell(cell);

    chunk = new Chunk();
    chunk.setFont(generalBoldFont);
    chunk.append("Institution");

    cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(chunk);
    table.addCell(cell);

    chunk = new Chunk();
    chunk.setFont(generalBoldFont);
    chunk.append("Result");

    cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(chunk);
    table.addCell(cell);

    chunk = new Chunk();
    chunk.setFont(generalBoldFont);
    chunk.append("Year");

    cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(chunk);
    table.addCell(cell);

    for(AcademicInformation academicInformation1 : academicInformation) {
      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(mAcademicDegreeManager.get(academicInformation1.getDegreeId()).getDegreeShortName());

      cell = new PdfPCell();
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);
      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(academicInformation1.getInstitute());

      cell = new PdfPCell();
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);
      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(academicInformation1.getResult() == null ? "" : academicInformation1.getResult());

      cell = new PdfPCell();
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);
      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(academicInformation1.getPassingYear());

      cell = new PdfPCell();
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);
      table.addCell(cell);
    }

    document.add(table);

    // ------------------------------------------------------ End
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------publication information
    // ----------------------------

    prepareSectionHeader(new PdfPTable(1), new Chunk(), new PdfPCell(), new Paragraph(), document, titleSmallFont,
        "Publication");

    table = new PdfPTable(new float[] {new Float(0.4), new Float(10)});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    int increment = 1;
    for(PublicationInformation publicationInformation1 : publicationInformation) {
      String inc = increment++ + ".";
      chunk = new Chunk();
      chunk.setFont(generalBoldFont);
      chunk.append(inc);

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(publicationInformation1.getTitle());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);
    }
    increment = 1;
    document.add(table);

    // -------------------------------------------------End
    // -----------------------------------------------------

    prepareSectionHeader(new PdfPTable(1), new Chunk(), new PdfPCell(), new Paragraph(), document, titleSmallFont,
        "Training");

    prepareTrainingInformation(trainingInformation, document, new Chunk(), new PdfPCell(), generalFont,
        generalBoldFont, table, increment, TrainingCategory.LOCAL.getId());
    prepareTrainingInformation(trainingInformation, document, new Chunk(), new PdfPCell(), generalFont,
        generalBoldFont, table, increment, TrainingCategory.Foreign.getId());

    // ----------------------------- End
    // -------------------------------------------------------------

    // -------------------------------- award Information
    // --------------------------------------------

    document.close();
    baos.writeTo(pOutputStream);
  }

  private void prepareTrainingInformation(List<TrainingInformation> trainingInformation, Document document,
      Chunk chunk, PdfPCell cell, Font generalFont, Font generalBoldFont, PdfPTable table, int increment, int type)
      throws DocumentException {
    table = new PdfPTable(1);
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    chunk = new Chunk();
    chunk.setFont(generalBoldFont);
    if(type == TrainingCategory.LOCAL.getId()) {

      chunk.append("Local");

    }
    else {
      chunk.append("Foreign");
    }
    cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.addElement(chunk);
    table.addCell(cell);
    document.add(cell);

    table = new PdfPTable(new float[] {new Float(0.4), new Float(1.2), new Float(10)});
    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    for(TrainingInformation trainingInformation1 : trainingInformation) {
      String inc = increment++ + ".";
      chunk = new Chunk();
      chunk.setFont(generalBoldFont);
      chunk.append(inc);

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append("Subject: ");

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(trainingInformation1.getTrainingName());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      table.addCell("");

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append("Institution:");

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(trainingInformation1.getTrainingInstitute());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      table.addCell("");

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append("From:");

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(trainingInformation1.getTrainingFromDate());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      table.addCell("");

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append("To:");

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(trainingInformation1.getTrainingToDate());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      table.addCell("");

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append("Duration:");

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);

      chunk = new Chunk();
      chunk.setFont(generalFont);
      chunk.append(trainingInformation1.getTrainingDurationString());

      cell = new PdfPCell();
      cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      cell.setBorder(PdfPCell.NO_BORDER);
      cell.addElement(chunk);

      table.addCell(cell);
    }

    document.add(table);
  }

  private void emptyLine(Paragraph p, int number) {
    for(int i = 0; i < number; i++) {
      p.add(new Paragraph(" "));
    }
  }

  private PdfPCell prepareCell(Chunk chunk, PdfPCell innerCell, String text, Font font) {
    chunk.setFont(font);
    chunk.append(text);
    innerCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    innerCell.setBorder(PdfPCell.NO_BORDER);
    innerCell.addElement(chunk);
    return innerCell;
  }

  private void prepareSectionHeader(PdfPTable table, Chunk chunk, PdfPCell cell, Paragraph paragraph,
      Document document, Font titleSmallFont, String text) throws DocumentException {
    paragraph = new Paragraph();
    emptyLine(paragraph, 1);
    document.add(paragraph);

    table.setWidthPercentage(100);
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

    chunk.setFont(titleSmallFont);
    chunk.append(text);

    paragraph.add(chunk);

    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    cell.setBorder(Rectangle.TOP);
    cell.addElement(paragraph);

    table.addCell(cell);
    document.add(table);

    paragraph = new Paragraph();
    emptyLine(paragraph, 1);
    document.add(paragraph);
  }
}
