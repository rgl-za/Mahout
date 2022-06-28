package com.example.demo.itemrecommend;

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

public class ItemRecommend { // convert 클래스에서 만든 csv를 가지고 유사성을 검사해서 recommend 해주는 클래스
	public static void main(String[] args) {
		try {
			DataModel dm = new FileDataModel(new File("data/movies.csv")); // 데이터 모델 생성
			TanimotoCoefficientSimilarity sim = new TanimotoCoefficientSimilarity(dm); // 유사도 모델 선택
			GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dm, sim); // 추천기 선택: ItemBased
			int x = 1;
			for (LongPrimitiveIterator items = dm.getItemIDs(); items.hasNext();) { // 데이터 모델 내의 item들의 iterator를 단계별 이동하여 추천 아이템 제공
				long itemId = items.nextLong(); // 현재 item ID
				List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 5); // 현재 item ID와 가장 유사한 5개 아이템 추천
				for (RecommendedItem recommendation : recommendations) { // 유사한 아이템 출력  = 현재 아이템 ID | 추천된 아이템 ID | 유사도
					System.out.println(itemId + "," + recommendation.getItemID() + "," + recommendation.getValue());
				}
				x++; // 아이템 ID 5까지 유사한 아이템들 5개씩
				 //if(x> 10) System.exit(1);
			}
		} catch (IOException e) {
			System.out.println("there was an error.");
			e.printStackTrace();
		} catch (TasteException e) {
			System.out.println("there was an Taste Exception.");
			e.printStackTrace();
		}
	}
}
