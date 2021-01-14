package im.prize.api.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import im.prize.api.application.dto.GwanboDataModel;
import im.prize.api.application.dto.GwanboType;
import im.prize.api.application.dto.GwanboUserModel;
import im.prize.api.application.dto.PdfResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class PdfServiceImpl implements PdfService {
    @Override
    public void read(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File f = new File(classLoader.getResource(fileName).getFile());

        ObjectMapper xmlMapper = new XmlMapper();
//        xmlMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        TypeFactory typeFactory = xmlMapper.getTypeFactory();

        JsonNode jsonNode = xmlMapper.readTree(f);
        List<PdfResponse> jsonNode1 = jsonNode.get("page").findValues("table").stream().map(x -> {
            List<List<String>> collect = ImmutableList.copyOf(x.get("tr").iterator())
                                                      .stream()
                                                      .map(y -> y.get("td"))
                                                      .filter(y -> y.isArray())
                                                      .map(y -> ImmutableList.copyOf(y.iterator()))
                                                      .map(y -> y.stream()
                                                                 .map(z -> z.isObject() ? z.get("") : z)
                                                                 .filter(Objects::nonNull)
                                                                 .map(z -> z.isArray() ? StringUtils.join(ImmutableList.copyOf(z.iterator())
                                                                                                                       .stream()
                                                                                                                       .map(c -> c.textValue())
                                                                                                                       .map(c -> c.replace(
                                                                                                                           "\n",
                                                                                                                           "")
                                                                                                                                  .replace(
                                                                                                                                      "(",
                                                                                                                                      "")
                                                                                                                                  .replace(
                                                                                                                                      ")",
                                                                                                                                      "")
                                                                                                                                  .trim())
                                                                                                                       .collect(Collectors.toList()),
                                                                                                          "/") : z.textValue())
                                                                 .filter(z -> !"".equals(z))
                                                                 .collect(Collectors.toList()))
                                                      .collect(Collectors.toList());
            return PdfResponse.builder()
                              .dataFileName(x.get("data-filename").textValue())
                              .dataPage(Integer.valueOf(x.get("data-page").textValue()))
                              .dataTable(Integer.valueOf(x.get("data-table").textValue()))
                              .row(collect)
                              .build();
        }).collect(Collectors.toList());
        List<List<String>> result = jsonNode1.stream().map(x -> x.getRow()).flatMap(Collection::stream).collect(Collectors.toList());
        int index = 0;
        int startIndex = 0;
        int endIndex = 0;
        List<List<List<String>>> temp = Lists.newArrayList();
        for (List<String> strings : result) {
            if ("소속".equals(strings.get(0))) {
                startIndex = index;
            }
            if ("총 계".equals(strings.get(0))) {
                endIndex = index;
            }
            if (startIndex != 0 & endIndex != 0) {
                List<List<String>> data = result.subList(startIndex, endIndex + 1);
                startIndex = 0;
                endIndex = 0;
                temp.add(data);
            }
            index++;
        }
        Map<GwanboUserModel, List<GwanboDataModel>> resultMap = Maps.newHashMap();
        int key = 0;
        for (List<List<String>> lists : temp) {
//            int buildingIndex = 0;
//            int carIndex = 0;
//            int bankIndex = 0;
//            int stockIndex = 0;
//            int totalIndex = 0;

            int checkIndex = 0;
            List<List<List<String>>> data = Lists.newArrayList();
            for (int i = 0; i < lists.size(); i++) {
                List<String> list = lists.get(i);
//                if ("소속".equals(list.get(0))) {
//                    // 소속
//                    List<List<String>> test1 = lists.subList(checkIndex, i);
//                    checkIndex = i;
//                    data.add(test1);
//                }
                if ("▶ 토지(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 건물(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 부동산에 관한 규정이 준용되는 권리와 자동차·건설기계·선박 및 항공기(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 현금(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 예금(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 증권(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 채무(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 채권(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 증권(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 회원권(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 골동품 및 예술품(소계)".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("▶ 고지거부 및 등록제외사항".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if ("총 계".equals(list.get(0))) {
                    List<List<String>> test1 = lists.subList(checkIndex, i);
                    checkIndex = i;
                    data.add(test1);
                }
                if (i + 1 == lists.size()) {
                    List<List<String>> test1 = lists.subList(i, lists.size());
                    checkIndex = i;
                    data.add(test1);
                }
            }

            Map<String, List<List<List<String>>>> tempMap = Maps.newHashMap();
            List<GwanboDataModel> gwanboDataModels = Lists.newArrayList();
            GwanboUserModel userModel = GwanboUserModel.builder().build();
            for (List<List<String>> maps : data) {
                if (maps.size() > 0) {
                    if ("소속".equals(maps.get(0).get(0))) {
                        if (maps.get(0).size() > 5) {
                            userModel = this.getUserModel(maps.get(0));
                        }
                    }
                    gwanboDataModels.addAll(this.getKey(maps));
//                    tempMap.put(String.valueOf(key++), data);
                }
            }
            if (gwanboDataModels.size() > 0) {
                resultMap.put(userModel, gwanboDataModels);
            }
        }

        List<GwanboDataModel> dataModels = resultMap.entrySet().stream().map((entry) -> entry.getValue().stream().map(x -> {
            x.setCompany(entry.getKey().getCompany());
            x.setJob(entry.getKey().getJob());
            x.setName(entry.getKey().getName());
            return x;
        }).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());

        System.out.println();
    }

    private GwanboUserModel getUserModel(List<String> data) {
        if (data.size() > 5) {
            return GwanboUserModel.builder()
                                  .company(data.get(1))
                                  .job(data.get(3))
                                  .name(data.get(5))
                                  .build();
        }
        return null;
    }

    private List<GwanboDataModel> getKey(List<List<String>> data) {
        List<GwanboDataModel> result = Lists.newArrayList();
        String type = data.get(0).get(0);
        if (/*"▶ 부동산에 관한 규정이 준용되는 권리와 자동차·건설기계·선박 및 항공기(소계)".equals(type) || "▶ 토지(소계)".equals(type) || */"▶ 건물(소계)".equals(type)) {
            GwanboType gwanboType = GwanboType.BUILDING;
            if ("▶ 부동산에 관한 규정이 준용되는 권리와 자동차·건설기계·선박 및 항공기(소계)".equals(type)) {
                gwanboType = GwanboType.CAR;
            }
            if ("▶ 토지(소계)".equals(type)) {
                gwanboType = GwanboType.LAND;
            }
            if ("▶ 건물(소계)".equals(type)) {
                gwanboType = GwanboType.BUILDING;
            }
            List<List<String>> temp = data.subList(1, data.size());
            for (List<String> strings : temp) {
                GwanboDataModel.GwanboDataModelBuilder builder = GwanboDataModel.builder().gwanboType(gwanboType);
                if (strings.size() > 3) {
                    String address = strings.get(2);
//                    String[] splitAddress = address.split(" ");
//                    String addr = splitAddress[0];
//                    String area = splitAddress[splitAddress.length - 1];
//                    address = address.replace(area, "").replace("건물", "").trim();

//                    char[] chars = address.toCharArray();
//                    int numIndex = 0;
//                    for (char aChar : chars) {
//                        if(NumberUtils.isDigits(String.valueOf(aChar))) {
//                            break;
//                        }
//                        numIndex++;
//                    }
////                    area = area.replace("m2", "");
//                    String addr = address.substring(0, numIndex);
//                    String area = address.substring(numIndex);
                    String[] splitAddress = address.split("건물");
                    String addr = null;
                    String landArea = null;
                    String buildingArea = null;
                    if (splitAddress.length > 1) {
                        addr = splitAddress[0].trim();
                        String[] buildingAddr = addr.split("대지");
                        if (buildingAddr.length > 1) {
                            addr = buildingAddr[0].trim();
                            landArea = buildingAddr[1].trim()
                                                      .replace("중/", "중")
                                                      .replace("m2", "")
                                                      .replaceAll("\\p{Z}", "");
                        }
                        buildingArea = splitAddress[1].trim()
                                                      .replace("중/", "중")
                                                      .replace("m2", "")
                                                      .replaceAll("\\p{Z}", "");

                        addr = this.getOnlyDigit(addr);
                        landArea = this.getOnlyDigit(landArea);
                        buildingArea = this.getOnlyDigit(buildingArea.split("중")[0]);
                    }

                    String price = strings.get(3).replace(",", "");
                    String realPrice = strings.get(3).replace(",", "");
                    String[] splitPrice = price.split("/");
                    if (splitPrice.length > 1) {
                        price = splitPrice[0];
                        realPrice = splitPrice[1];
                    }
                    builder = builder.date("")
                                     .owner(strings.get(0))
                                     .type(strings.get(1))
                                     .address(addr)
                                     .landArea(landArea)
                                     .buildingArea(buildingArea)
                                     .price(price)
                                     .realPrice(realPrice);
                }
                if (strings.size() > 4) {
                    builder.etc(strings.get(4));
                }
                result.add(builder.build());
            }
//            return result;
        }
//        if ("▶ 부동산에 관한 규정이 준용되는 권리와 자동차·건설기계·선박 및 항공기(소계)".equals(gwanboType)) {
//            System.out.println();
////            return "3";
//        }
//        if ("▶ 현금(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            for (List<String> strings : temp) {
//                Integer price = strings.subList(2, strings.size())
//                                       .stream()
//                                       .map(x -> NumberUtils.toInt(x.replace(",", "")))
//                                       .reduce((a, b) -> a + b)
//                                       .orElse(0);
//                result.add(GwanboDataModel.builder()
//                                          .gwanboType(GwanboType.CASH)
//                                          .owner(strings.get(0))
//                                          .type(strings.get(1))
//                                          .price(String.valueOf(price))
//                                          .build());
//            }
//        }
//        if ("▶ 예금(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            for (List<String> strings : temp) {
//                if(strings.size() > 1) {
//                    List<String> items = Lists.newArrayList(strings.get(1).replace(",/", ", ").trim().split(", "));
//                    for (String item : items) {
//                        String[] splitItem = item.trim().split(" ");
//                        if(splitItem.length > 1 && NumberUtils.isDigits(splitItem[1])) {
//                            result.add(GwanboDataModel.builder()
//                                                      .gwanboType(GwanboType.BANK)
//                                                      .owner(strings.get(0))
//                                                      .name(splitItem[0])
//                                                      .price(splitItem[1].replace(",", ""))
//                                                      .build());
//                        }
//                    }
//                }
//            }
////            return "5";
//        }
//        if ("▶ 증권(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            for (List<String> strings : temp) {
//                if(strings.size() > 3) {
//                    List<String> items = Lists.newArrayList(strings.get(2).replace(",/", ", ").split(", "));
//                    for (String item : items) {
//                        String[] splitItem = item.trim().split(" ");
//                        result.add(GwanboDataModel.builder()
//                                                  .gwanboType(GwanboType.STOCK)
//                                                  .owner(strings.get(0))
//                                                  .name(splitItem[0])
//                                                  .type(strings.get(1))
//                                                  .price(splitItem[splitItem.length - 1].replace(",", ""))
//                                                  .build());
//                    }
//                }
//            }
//            System.out.println();
////            return "6";
//        }
//        if ("▶ 채무(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            for (List<String> strings : temp) {
//                if("건물임대채무".equals(strings.get(1))) {
//                    result.add(GwanboDataModel.builder()
//                                              .gwanboType(GwanboType.DEBT)
//                                              .owner(strings.get(0))
//                                              .type(strings.get(1))
//                                              .name(strings.get(2).replace("임대보증금", "").trim())
//                                              .price(strings.get(3).replace(",", ""))
//                                              .build());
//                }
//                else {
//                    List<String> items = Lists.newArrayList(strings.get(2).replace(",/", ", ").split(", "));
//                    for (String item : items) {
//                        String[] splitItem = item.trim().split(" ");
//                        result.add(GwanboDataModel.builder()
//                                                  .gwanboType(GwanboType.DEBT)
//                                                  .owner(strings.get(0))
//                                                  .name(splitItem[0])
//                                                  .type(strings.get(1))
//                                                  .price(splitItem[splitItem.length - 1].replace(",", ""))
//                                                  .build());
//                    }
//                }
//            }
////            return "7";
//        }
//        if ("▶ 채권(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            System.out.println();
////            return "8";
//        }
//        if ("▶ 증권(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            System.out.println();
////            return "9";
//        }
//        if ("▶ 회원권(소계)".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            System.out.println();
////            return "10";
//        }
//        if ("▶ 고지거부 및 등록제외사항".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            System.out.println();
////            return "11";
//        }
//        if ("총 계".equals(type)) {
//            List<List<String>> temp = data.subList(1, data.size());
//            System.out.println();
////            return "12";
//        }
        return result.stream().filter(x -> "아파트".equals(x.getType())).collect(Collectors.toList());
    }

    public static String getOnlyDigit(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        if (str != null && str.length() != 0) {

            Pattern p = Pattern.compile("[0-9]|[가-힣 ]|[.]|[|]]");

            Matcher m = p.matcher(str);

            while (m.find()) {

                sb.append(m.group());

            }

        }

        return sb.toString();

    }
}
