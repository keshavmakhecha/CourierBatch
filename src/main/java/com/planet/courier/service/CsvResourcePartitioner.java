package com.planet.courier.service;

import static com.planet.courier.constant.CourierConstant.FILE_NAME;
import static com.planet.courier.constant.CourierConstant.FOLDER_PATH;
import static com.planet.courier.util.CourierUtil.getNoOfLines;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class CsvResourcePartitioner implements Partitioner {

	private static final Logger logger = LoggerFactory.getLogger(CsvResourcePartitioner.class);

	public Map<String, ExecutionContext> partition(int gridSize) {
		
		Map<String, ExecutionContext> result = new HashMap<>();

        int noOfLines = 0;
        try {
            noOfLines = getNoOfLines(FOLDER_PATH.concat(FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int firstLine = 1;
        int lastLine = gridSize;
        int partitionNumber = 0;

        while(firstLine < noOfLines) {

            if(lastLine >= noOfLines) {
                lastLine = noOfLines;
            }

            logger.info("Partition number : {}, first line is : {}, last  line is : {} ", partitionNumber, firstLine, lastLine);

            ExecutionContext value = new ExecutionContext();

            value.putLong("partition_number", partitionNumber);
            value.putLong("first_line", firstLine);
            value.putLong("last_line", lastLine);

            result.put("PartitionNumber-" + partitionNumber, value);

            firstLine = firstLine + gridSize;
            lastLine = lastLine + gridSize;
            partitionNumber++;
        }

        logger.info("No of lines {}", noOfLines);

        return result;
	}
	
	

}
