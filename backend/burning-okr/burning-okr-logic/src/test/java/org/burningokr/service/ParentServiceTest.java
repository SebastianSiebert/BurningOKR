package org.burningokr.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.burningokr.model.okr.Objective;
import org.burningokr.model.structures.Company;
import org.burningokr.model.structures.CompanyStructure;
import org.burningokr.model.structures.Department;
import org.burningokr.service.structureutil.ParentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParentServiceTest {

  @Mock private ParentService mockParentService;

  @InjectMocks private ParentService parentService;

  @Before
  public void reset() {
    parentService = new ParentService();
    buildTestCompany();
  }

  @Test
  public void test_validateParentObjective_expectsNothing() {
    when(mockParentService.isParentObjectiveLegal(any(Objective.class), any(Objective.class)))
        .thenReturn(true);
    doCallRealMethod()
        .when(mockParentService)
        .validateParentObjective(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    mockParentService.validateParentObjective(testObjective, testObjective);

    verify(mockParentService).isParentObjectiveLegal(any(Objective.class), any(Objective.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_validateParentObjective_expectsFalse() {
    when(mockParentService.isParentObjectiveLegal(any(Objective.class), any(Objective.class)))
        .thenReturn(false);
    doCallRealMethod()
        .when(mockParentService)
        .validateParentObjective(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    mockParentService.validateParentObjective(testObjective, testObjective);

    verify(mockParentService).isParentObjectiveLegal(any(Objective.class), any(Objective.class));
  }

  @Test
  public void test_isParentObjectiveLegal_expectsTrue() {
    when(mockParentService.isStructureChildStructure(
            any(CompanyStructure.class), any(CompanyStructure.class)))
        .thenReturn(true);
    doCallRealMethod()
        .when(mockParentService)
        .isParentObjectiveLegal(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    CompanyStructure testParentStructure = new Department();
    testObjective.setParentStructure(testParentStructure);
    Assert.assertTrue(mockParentService.isParentObjectiveLegal(testObjective, testObjective));

    verify(mockParentService)
        .isStructureChildStructure(any(CompanyStructure.class), any(CompanyStructure.class));
  }

  @Test
  public void test_isParentObjectiveLegal_expectsFalse() {
    when(mockParentService.isStructureChildStructure(
            any(CompanyStructure.class), any(CompanyStructure.class)))
        .thenReturn(false);
    doCallRealMethod()
        .when(mockParentService)
        .isParentObjectiveLegal(any(Objective.class), any(Objective.class));

    Objective testObjective = new Objective();
    CompanyStructure testParentStructure = new Department();
    testObjective.setParentStructure(testParentStructure);
    Assert.assertFalse(mockParentService.isParentObjectiveLegal(testObjective, testObjective));

    verify(mockParentService)
        .isStructureChildStructure(any(CompanyStructure.class), any(CompanyStructure.class));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjCompany_expectsTrue() {
    Assert.assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveCompany));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD2_expectsTrue() {
    Assert.assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD2));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD22_expectsTrue() {
    Assert.assertTrue(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD22));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD1_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD1));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD22_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD221));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToOrigin_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeOrigin));
  }

  @Test
  public void test_isParentObjectiveLegal_OriginToObjD221_expectsFalse() {
    Assert.assertFalse(parentService.isParentObjectiveLegal(treeOrigin, treeObjectiveD221));
  }

  ///////////////////////////////
  /////   Tree-Structures  //////
  // ////////////////////////////
  /////   For a visualisation look at JTK_Backend/ObjectiveServiceTest_TreeStructure
  // ////////////////////////////

  Company treeCompany;
  Department treeDepartment1;
  Department treeDepartment2;
  Department treeDepartment22;
  Department treeDepartment221;
  Objective treeObjectiveCompany;
  Objective treeObjectiveD1;
  Objective treeObjectiveD2;
  Objective treeObjectiveD22;
  Objective treeObjectiveD221;
  Objective treeOrigin;

  private void buildTestCompany() {
    treeCompany = new Company();

    treeObjectiveCompany = new Objective();
    treeCompany.getObjectives().add(treeObjectiveCompany);
    treeObjectiveCompany.setParentStructure(treeCompany);

    treeDepartment1 = new Department();
    treeCompany.getDepartments().add(treeDepartment1);
    treeDepartment1.setParentStructure(treeCompany);

    treeObjectiveD1 = new Objective();
    treeDepartment1.getObjectives().add(treeObjectiveD1);
    treeObjectiveD1.setParentStructure(treeDepartment1);

    treeDepartment2 = new Department();
    treeCompany.getDepartments().add(treeDepartment2);
    treeDepartment2.setParentStructure(treeCompany);

    treeObjectiveD2 = new Objective();
    treeDepartment2.getObjectives().add(treeObjectiveD2);
    treeObjectiveD2.setParentStructure(treeDepartment2);

    treeDepartment22 = new Department();
    treeDepartment2.setDepartments(Arrays.asList(treeDepartment22));
    treeDepartment22.setParentStructure(treeDepartment2);

    treeObjectiveD22 = new Objective();
    treeDepartment22.getObjectives().add(treeObjectiveD22);
    treeObjectiveD22.setParentStructure(treeDepartment22);

    treeDepartment221 = new Department();
    treeDepartment22.setDepartments(Arrays.asList(treeDepartment221));
    treeDepartment221.setParentStructure(treeDepartment22);

    treeObjectiveD221 = new Objective();
    treeDepartment221.getObjectives().add(treeObjectiveD221);
    treeObjectiveD221.setParentStructure(treeDepartment221);

    treeOrigin = new Objective();
    treeDepartment221.getObjectives().add(treeOrigin);
    treeOrigin.setParentStructure(treeDepartment221);
  }
}
