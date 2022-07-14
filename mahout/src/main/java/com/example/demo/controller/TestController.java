package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.CSVReader;


@RestController
public class TestController {
	
	@GetMapping("/test")
	public String test() {
		CSVReader csvReader = new CSVReader();
        List<List<String>> a = csvReader.readCSV();
        
        for(int i = 0; i < a.size(); i++) {
        	System.out.println(a.get(i));
        }
        
		try {
			DataModel dm = new FileDataModel(new File("data/test2.csv"));
			TanimotoCoefficientSimilarity sim = new TanimotoCoefficientSimilarity(dm);
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dm, sim);
			int x = 1;
			for (LongPrimitiveIterator items = dm.getItemIDs(); items.hasNext();) {
				long itemId = items.nextLong();
				List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 5);
				for (RecommendedItem recommendation : recommendations) {
					System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
				}
				x++;
				// if(x> 10) System.exit(1);
			}
		} catch (IOException e) {
			System.out.println("there was an error.");
			e.printStackTrace();
		} catch (TasteException e) {
			System.out.println("there was an Taste Exception.");
			e.printStackTrace();
		}
		return "hello";
	}
}
