package org.burningokr.service.structureutil;

import org.burningokr.model.cycles.Cycle;
import org.burningokr.model.okr.KeyResult;
import org.burningokr.model.okr.Objective;
import org.burningokr.model.structures.Company;
import org.burningokr.model.structures.CompanyStructure;
import org.burningokr.model.structures.Department;
import org.springframework.stereotype.Service;

@Service
public class EntityCrawlerService {

  public Cycle getCycleOfCompany(Company companyToCheck) {
    return companyToCheck.getCycle();
  }

  /**
   * Gets the Cycle of a Department.
   *
   * @param departmentToCheck a {@link Department} object
   * @return a {@link Cycle} object
   */
  public Cycle getCycleOfDepartment(Department departmentToCheck) {
    CompanyStructure parentStructure = departmentToCheck.getParentStructure();
    if (parentStructure instanceof Company) {
      return this.getCycleOfCompany((Company) parentStructure);
    } else {
      return this.getCycleOfDepartment((Department) parentStructure);
    }
  }

  private Cycle getCycleOfParentStructure(CompanyStructure parentStructure) {
    if (parentStructure instanceof Company) {
      return this.getCycleOfCompany((Company) parentStructure);
    } else {
      return this.getCycleOfDepartment((Department) parentStructure);
    }
  }

  public Cycle getCycleOfObjective(Objective objectiveToCheck) {
    return this.getCycleOfParentStructure(objectiveToCheck.getParentStructure());
  }

  public Cycle getCycleOfKeyResult(KeyResult keyResultToCheck) {
    return this.getCycleOfObjective(keyResultToCheck.getParentObjective());
  }

  /**
   * Gets the Company of a Department.
   *
   * @param departmentToCheck a {@link Department} object
   * @return a {@link Company} object
   */
  public Company getCompanyOfDepartment(Department departmentToCheck) {
    CompanyStructure parentStructure = departmentToCheck.getParentStructure();
    if (parentStructure instanceof Company) {
      return (Company) parentStructure;
    } else {
      return this.getCompanyOfDepartment((Department) parentStructure);
    }
  }
}
