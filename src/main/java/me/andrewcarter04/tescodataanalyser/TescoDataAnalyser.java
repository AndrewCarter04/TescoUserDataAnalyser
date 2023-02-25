package me.andrewcarter04.tescodataanalyser;

import org.json.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TescoDataAnalyser {

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter file path: ");
        String filePath =  scanner.nextLine();

        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(sb.toString());
        JSONArray purchases = jsonObject.getJSONArray("Purchase").getJSONArray(0);

        Map<String, Integer> productQuantity = new HashMap<String, Integer>();
        Map<String, Integer> storeQuantity = new HashMap<String, Integer>();

        String highestPurchase = "";
        Float highestPurchasePrice = 0F;

        Float averageSpend = 0F;

        for (int i = 0; i < purchases.length(); i++) {
            JSONObject purchase = purchases.getJSONObject(i);
            //System.out.println(purchase.toString();
            JSONArray products = purchase.getJSONArray("product");
            if (storeQuantity.containsKey(purchase.getString("storeName"))) {
                storeQuantity.put(purchase.getString("storeName"), (storeQuantity.get(purchase.getString("storeName")) + 1));
            } else {
                storeQuantity.put(purchase.getString("storeName"), 1);
            }
            for (int j = 0; j < products.length(); j++) {
                JSONObject product = products.getJSONObject(j);
                if (Float.parseFloat(product.getString("price")) > highestPurchasePrice) {
                    highestPurchasePrice = Float.parseFloat(product.getString("price"));
                    highestPurchase = product.getString("name");
                }
                averageSpend += Float.parseFloat(product.getString("price"));
                if (productQuantity.containsKey(product.getString("name"))) {
                    productQuantity.put(product.getString("name"), (productQuantity.get(product.getString("name")) + product.getInt("quantity")));
                } else {
                    productQuantity.put(product.getString("name"), product.getInt("quantity"));
                }
            }
        }

        Map<String, Integer> sortedProducts = productQuantity.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        Map<String, Integer> sortedStores = storeQuantity.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        Float totalSpend = averageSpend;
        averageSpend /= purchases.length();

        DecimalFormat df = new DecimalFormat("##.00");

        System.out.println();
        System.out.println("Total Spend: £" + df.format(totalSpend));
        System.out.println("Average Spend: £" + df.format(averageSpend));
        System.out.println();
        System.out.println("Most Expensive Product: " + highestPurchase + " - £" + df.format(highestPurchasePrice));
        System.out.println();
        System.out.println("Top Purchases: ");
        Integer i = 1;
        for (Map.Entry<String, Integer> entry : sortedProducts.entrySet()) {
            System.out.println(i.toString() + ": " + entry.getKey() + " - Quantity: " + entry.getValue());
            i++;
            if (i == 11) {
                break;
            }
        }
        System.out.println();
        System.out.println("Top Stores: ");
        i = 1;
        for (Map.Entry<String, Integer> entry : sortedStores.entrySet()) {
            System.out.println(i.toString() + ": " + entry.getKey() + " - Visits: " + entry.getValue());
            i++;
        }



    }
}