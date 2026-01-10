package saas.app.engine.scraper.util;

import java.math.BigDecimal;

public class PriceParser {

    public static BigDecimal parse(String priceText){
        if (priceText == null && priceText.isEmpty()){
            return BigDecimal.ZERO;
        }
        //limpia todo lo que no sea numero, coma o punto
        String cleanString = priceText.replaceAll("[^0-9,.]", "");


        try { //esto lo que va a hacer es detectar el formato de la moneda
            if(cleanString.contains(",") && cleanString.lastIndexOf(",") > cleanString.lastIndexOf(".")){

            cleanString = cleanString.replace(".", "").replace(",", ".");

            } else if (cleanString.contains(".") && cleanString.contains(",")){
                cleanString = cleanString.replace(",", "");
            } else if (cleanString.contains(",")) {
                cleanString = cleanString.replace(",", ".");
            }

            return new BigDecimal(cleanString);

        } catch (Exception e){
            System.err.println("Error parseando el precio" + priceText);

            return BigDecimal.ZERO;
        }


}


}