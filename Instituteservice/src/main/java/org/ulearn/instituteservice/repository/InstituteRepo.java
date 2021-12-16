package org.ulearn.instituteservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ulearn.instituteservice.entity.InstituteEntity;
import org.ulearn.instituteservice.entity.InstituteGlobalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstituteRepo extends JpaRepository<InstituteEntity, Long> {
	Optional<InstituteEntity> findByInstName(String instName);
	
	Optional<InstituteEntity> findByInstEmail(String instEmail);	
	
	@Query(value = "SELECT instObj FROM InstituteEntity instObj INNER JOIN InstituteAdminEntity instAmdObj on instAmdObj.instId = instObj.instId WHERE instObj.isDeleted = ?1")
	Page<InstituteEntity> findByAllInst(int isDeleted,Pageable pagingSort);
	
//	@Query(value = "SELECT instObj FROM InstituteEntity instObj LEFT JOIN LicenseEntity instLicObj on instLicObj.instId = instObj.instId WHERE instLicObj.lcEndDate < CURRENT_DATE OR instLicObj.instId = null AND instObj.isDeleted = 0 AND instObj.isActive = 1 AND instObj.instStatus = 1")
	@Query(value = "SELECT instObj FROM InstituteEntity instObj LEFT JOIN LicenseEntity instLicObj on instLicObj.instId = instObj.instId WHERE instLicObj.instId = null AND instObj.isDeleted = 0 AND instObj.isActive = 1 AND instObj.instStatus = 1")
	List<InstituteEntity> findByListInst();
	
//	@Query(value = "SELECT instObj FROM InstituteEntity instObj LEFT JOIN LicenseEntity instLicObj on instLicObj.instId = instObj.instId WHERE instLicObj.lcEndDate >= CURRENT_DATE AND instObj.isDeleted = 0 AND instObj.isActive = 1 AND instObj.instStatus = 1")
	@Query(value = "SELECT instObj FROM InstituteEntity instObj LEFT JOIN LicenseEntity instLicObj on instLicObj.instId = instObj.instId WHERE instLicObj.instId != null AND instObj.isDeleted = 0 AND instObj.isActive = 1 AND instObj.instStatus = 1")
	List<InstituteEntity> findByLicenseListInst();
	
	@Query(value = "SELECT instObj FROM InstituteEntity instObj LEFT JOIN LicenseEntity instLicObj on instLicObj.instId = instObj.instId WHERE instObj.isDeleted = 0 AND instObj.isActive = 1 AND instObj.instStatus = 1")
	List<InstituteEntity> findByEditLicenseListInst();

	@Query(value = "select * from tbl_institutes where INST_ID != ? and INST_NAME = ? and IS_DELETED = 0",nativeQuery = true)
	List<InstituteEntity> findByInstUnqName(long instId,String instName); 
	
	@Query(value = "select instD from InstituteEntity instD where instD.instId != ?1 and instD.instEmail = ?2")
	List<InstituteEntity> findByInstUnqEmail(long instId,String instEmail);
	 
	@Query(value = "select instAdrObj from InstituteEntity instD INNER JOIN InstituteAddressEntity instAdrObj on instAdrObj.instId = instD.instId INNER JOIN InstituteAdminEntity instAmdObj on instAmdObj.instId = instD.instId")
	List<InstituteGlobalEntity> findByAllInstQuery();	

	@Query(value = "select * from tbl_institutes where IS_DELETED = 0",nativeQuery = true)
	List<InstituteGlobalEntity> findByInstUnq();
	
	@Query(value = "select tbl_institutes.*,tbl_inst_addr.* from tbl_institutes INNER JOIN tbl_inst_addr on tbl_institutes.INST_ID = tbl_inst_addr.INST_ID",nativeQuery = true)
	List<InstituteEntity> func();	

	@Query("SELECT instObj FROM InstituteEntity instObj INNER JOIN InstituteAdminEntity instAmdObj on instAmdObj.instId = instObj.instId WHERE CONCAT(instAmdObj.amdUsername,instObj.instName,instObj.instEmail,instObj.instMnum,instObj.instCnum,instObj.instWebsite,instObj.isntRegDate) LIKE %?1% AND instObj.isDeleted = ?2")
    Page<InstituteEntity> Search(String sortKey, int isDeleted,Pageable pageable);
	
	@Query("SELECT instObj FROM InstituteEntity instObj WHERE instObj.instId =?1 AND instObj.instEmail = ?2")
    List<InstituteEntity> findByIdAndEmail(long instId, String instEmail);
	
	

		
}
