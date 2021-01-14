package im.prize.api.interfaces;

import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by hyoseok.choi on 2017-08-09.
 */
public class MoneyConverter {

    /**
     * 4000 -> 4,000, 400000 -> 40억, -4000 -> -4,000, -400000 -> -40억
     *
     * @ param p 만원 단위의 금액 문자열
     */
    public static String convertToLandMoneyAbbreviation(String priceTenThousandUnit) {
        if (StringUtils.isNumeric(priceTenThousandUnit.replace("-", ""))) {
            return MoneyConverter.convertToLandMoneyAbbreviation(Integer.parseInt(priceTenThousandUnit));
        }
        return priceTenThousandUnit;
    }

    /**
     * 4000 -> 4,000, 400000 -> 40억, -4000 -> -4,000, -400000 -> -40억
     *
     * @ param p 만원 단위의 금액 정수
     */
    public static String convertToLandMoneyAbbreviation(int priceTenThousandUnit) {
        String minusSign = "";
        if (priceTenThousandUnit < 0) {
            minusSign = "-";
            priceTenThousandUnit = -priceTenThousandUnit;
        }
        NumberFormat currencyFormat = NumberFormat.getIntegerInstance(Locale.KOREA);
        if (priceTenThousandUnit >= 10000) {
            int bigPrice = priceTenThousandUnit / 10000;
            int smallPrice = priceTenThousandUnit % 10000;
            return minusSign + (currencyFormat.format(bigPrice) + "억" + (smallPrice > 0 ? " " + currencyFormat.format(smallPrice) : "")).trim();
        } else {
            return minusSign + currencyFormat.format(priceTenThousandUnit);
        }
    }

    /**
     * 4000/5000 -> 4,000/5,000
     *
     * @ param p 만원 단위의 금액 문자열
     */
    public static String convertToLandMoneyAbbreviationWithSlash(String priceTenThousandUnitWithSlash) {
        if (StringUtils.isNotEmpty(priceTenThousandUnitWithSlash)) {
            String currencyFormmatedStr = "";
            String[] prcStrItems = priceTenThousandUnitWithSlash.replaceAll(",", "").split("/");
            int prcStrItemsLength = prcStrItems.length;
            for (int i = 0; i < prcStrItemsLength; i++) {
                if (i < prcStrItemsLength - 1) {
                    currencyFormmatedStr = currencyFormmatedStr + MoneyConverter.convertToLandMoneyAbbreviation(prcStrItems[i].trim()) +
                        "/";
                } else {
                    currencyFormmatedStr = currencyFormmatedStr + MoneyConverter.convertToLandMoneyAbbreviation(prcStrItems[i].trim());
                }
            }
            return currencyFormmatedStr;
        } else {
            return priceTenThousandUnitWithSlash;
        }
    }

    /**
     * 4000 -> 4,000, 400000 -> 40만원, -4000 -> -4,000, -400000 -> -40만원
     *
     * @ param p 원 단위의 금액 문자열
     */
    public static String convertToMoneyAbbreviation(String price) {
        if (StringUtils.isNumeric(price.replace("-", ""))) {
            return MoneyConverter.convertToMoneyAbbreviation(Double.parseDouble(StringUtils.trim(price)));
        }
        return price;
    }

    /**
     * 4000 -> 4,000, 400000 -> 40만원, -4000 -> -4,000, -400000 -> -40만원
     *
     * @ param p 원 단위의 금액 정수
     */
    public static String convertToMoneyAbbreviation(Double price) {
        String minusSign = "";
        if (price < 0) {
            minusSign = "-";
            price = -price;
        }
        NumberFormat currencyFormat = NumberFormat.getIntegerInstance(Locale.KOREA);

        if (Math.abs(price) > 0) {
            double hugePrice = Math.floor(price / 100000000);
            double bigPrice = Math.floor((price % 100000000) / 10000);
            double smallPrice = price % 10000;

            return minusSign + ((hugePrice >= 1 ? currencyFormat.format(hugePrice) + "억" : "") + (bigPrice >= 1 ?
                " " + currencyFormat.format(
                    bigPrice) + "만" : "") + (smallPrice >= 1 ? " " + currencyFormat.format(smallPrice) : "") + "원").trim();
        } else {
            return "- 원";
        }
    }
}