//package net.moboo.batch.infrastructure.jpa;
//
//import net.moboo.batch.domain.Region;
//import net.moboo.batch.domain.RegionType;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface RegionRepository extends JpaRepository<Region, Long> {
//    Region findByCode(String code);
//
//    Region findByCodeAndType(String code, RegionType type);
//
//    List<Region> findByType(RegionType type);
//
//    List<Region> findByCodeLikeAndType(String code, RegionType type);
//
//    List<Region> findByCodeLikeAndNameAndType(String code, String name, RegionType type);
//}
