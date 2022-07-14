package com.example.demo.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.example.demo.domain.RecommendDTO;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcCursorItemReader<RecommendDTO> reader(){
        JdbcCursorItemReader<RecommendDTO> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT p.KEYID as plantKeyId, NUM AS userNo, 1 as prefer \r\n"
				+ "from(\r\n"
				+ "	SELECT ROW_NUMBER() OVER (ORDER BY u.USERID) NUM, u.*\r\n"
				+ "	FROM USERS u\r\n"
				+ "	)u3 RIGHT OUTER JOIN USERKEYWORD u2 ON (u3.USERID = u2.USERID )\r\n"
				+ "					LEFT OUTER JOIN PLANTKEYWORD p ON u2.KEYID = p.KEYID \r\n"
				+ "ORDER BY num");
        reader.setRowMapper(new RowMapper<RecommendDTO>() {
            @Override
            public RecommendDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                RecommendDTO recommendDTO = new RecommendDTO();
                recommendDTO.setUserNo(rs.getInt("userNo"));
                recommendDTO.setPlantKeyId(rs.getInt("plantKeyId"));
                recommendDTO.setPrefer(rs.getInt("prefer"));
                return recommendDTO;
            }
        });
        return reader;
    }
    
    @Bean
    public JdbcCursorItemReader<RecommendDTO> reader2(){
        JdbcCursorItemReader<RecommendDTO> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT DISTINCT NUM AS userNo, u3.USERID \r\n"
				+ "from(\r\n"
				+ "	SELECT ROW_NUMBER() OVER (ORDER BY u.USERID) NUM, u.*\r\n"
				+ "	FROM USERS u\r\n"
				+ "	)u3 RIGHT OUTER JOIN USERKEYWORD u2 ON (u3.USERID = u2.USERID )\r\n"
				+ "					LEFT OUTER JOIN PLANTKEYWORD p ON u2.KEYID = p.KEYID \r\n"
				+ "ORDER BY num");
        reader.setRowMapper(new RowMapper<RecommendDTO>() {
            @Override
            public RecommendDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                RecommendDTO recommendDTO = new RecommendDTO();
                recommendDTO.setUserNo(rs.getInt("userNo"));
                recommendDTO.setUserId(rs.getString("userId"));
                return recommendDTO;
            }
        });
        return reader;
    }

    @Bean
    public FlatFileItemWriter<RecommendDTO> writer(){
        FlatFileItemWriter<RecommendDTO> writer = new FlatFileItemWriter<RecommendDTO>();
        writer.setResource(new FileSystemResource("C:\\Users\\hhh73\\StudioProjects\\Mahout\\mahout\\data\\test2.csv"));
        writer.setShouldDeleteIfEmpty(true);
        writer.setShouldDeleteIfExists(true);
        DelimitedLineAggregator<RecommendDTO> aggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<RecommendDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"plantKeyId", "userNo", "prefer"});
        aggregator.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(aggregator);
        return writer;
    }
    
    @Bean
    public FlatFileItemWriter<RecommendDTO> writer2(){
        FlatFileItemWriter<RecommendDTO> writer = new FlatFileItemWriter<RecommendDTO>();
        writer.setResource(new FileSystemResource("C:\\Users\\hhh73\\StudioProjects\\Mahout\\mahout\\data\\test3.csv"));
        DelimitedLineAggregator<RecommendDTO> aggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<RecommendDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"userNo","userId"});
        aggregator.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(aggregator);
        return writer;
    }

    @Bean
    public Step executeStep(){
        return stepBuilderFactory.get("executeStep").<RecommendDTO, RecommendDTO> chunk(10).reader(reader()).writer(writer())
                .build();
    }
    
    @Bean
    public Step executeStep2(){
        return stepBuilderFactory.get("executeStep2").<RecommendDTO, RecommendDTO> chunk(10).reader(reader2()).writer(writer2())
                .build();
    }

    @Bean
    public Job processJob(){
//        return jobBuilderFactory.get("processJob").incrementer(new RunIdIncrementer()).flow(executeStep()).end()
//                .build();
    	return jobBuilderFactory.get("processJob").start(executeStep()).next(executeStep2()).build();
    }
}
