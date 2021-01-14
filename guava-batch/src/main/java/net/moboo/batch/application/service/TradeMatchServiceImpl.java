package net.moboo.batch.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.moboo.batch.hgnn.repository.ApartmentMatchTable;
import net.moboo.batch.hgnn.repository.ApartmentMatchTableRepository;
import net.moboo.batch.hgnn.repository.AptTemp;
import net.moboo.batch.hgnn.repository.AptTempRepository;
import net.moboo.batch.hgnn.repository.RegionTemp;
import net.moboo.batch.hgnn.repository.RegionTempRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class TradeMatchServiceImpl implements TradeMatchService {
    private final ApartmentMatchTableRepository apartmentMatchTableRepository;
    private final RegionTempRepository regionTempRepository;
    private final AptTempRepository aptTempRepository;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    public TradeMatchServiceImpl(ApartmentMatchTableRepository apartmentMatchTableRepository,
                                 RegionTempRepository regionTempRepository,
                                 AptTempRepository aptTempRepository, ObjectMapper objectMapper,
                                 EntityManager entityManager) {
        this.apartmentMatchTableRepository = apartmentMatchTableRepository;
        this.regionTempRepository = regionTempRepository;
        this.aptTempRepository = aptTempRepository;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }

    @Override
    public List<ApartmentMatchTable> read() {
        return apartmentMatchTableRepository.findByHgnnIdIsNotNullAndLocationIsNull();
    }

    @Override
    public ApartmentMatchTable process(ApartmentMatchTable apartmentMatchTable) {
        List<AptTemp> byAptId = aptTempRepository.findByAptId(apartmentMatchTable.getHgnnId());
        Optional<AptTemp> byRegionCode = byAptId.stream().filter(y -> y.getAptId().equals(apartmentMatchTable.getHgnnId())).findFirst();
        if (byRegionCode.isPresent()) {
            log.error("valid : " + apartmentMatchTable.getId());
            try {
                Map<String, Map<String, Object>> data = (Map) objectMapper.readValue(byRegionCode.get().getData(), Map.class).get("result");
                double lat = (double) data.get("data").get("lat");
                double lng = (double) data.get("data").get("lng");
//                String pointWKT = String.format("POINT(%s %s)", lng, lat);
                Point point = this.createPoint(lng, lat);//(Point) new WKTReader().read(pointWKT);
                apartmentMatchTable.setLocation(point);
//                // 북동쪽 좌표 구하기
//                Location northEast = GeometryUtils.calculateByDirection(lat, lng, 2, CardinalDirection.NORTHEAST.getBearing());
//
//                // 남서쪽 좌표 구하기
//                Location southWest = GeometryUtils.calculateByDirection(lat, lng, 2, CardinalDirection.SOUTHWEST.getBearing());
//
//                double x1 = northEast.getLongitude();
//                double y1 = northEast.getLatitude();
//                double x2 = southWest.getLongitude();
//                double y2 = southWest.getLatitude();
//
////                List<ApartmentMatchTable> byMBRLocation = apartmentMatchTableRepository.findByMBRLocation(x1, y1, x2, y2);
//
//                Query query = entityManager.createNativeQuery("SELECT r.id \n" +
//                                                                  "FROM apartment_match_table AS r \n" +
//                                                                  "WHERE MBRContains(ST_LINESTRINGFROMTEXT(" + String.format("'LINESTRING(%f %f, %f %f)')", x1, y1, x2, y2) + ", r.location)"
//                    , ApartmentMatchTable.class);
                return apartmentMatchTable;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            log.error("### empty : " + apartmentMatchTable.getId());
        }
        return apartmentMatchTable;
    }

    @Override
    public List<ApartmentMatchTable> write(List<ApartmentMatchTable> apartmentMatchTableList) {
        System.out.println(apartmentMatchTableList.size());
        List<ApartmentMatchTable> apartmentMatchTables = apartmentMatchTableRepository.saveAll(apartmentMatchTableList);
        return apartmentMatchTables;
    }

    private Point createPoint(double lng, double lat) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(lng, lat));
    }
}
