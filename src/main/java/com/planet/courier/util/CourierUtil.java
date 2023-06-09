package com.planet.courier.util;

import static com.planet.courier.constant.CourierConstant.CHUNK_SIZE;
import static com.planet.courier.constant.CourierConstant.CORE_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.FILE_NAME;
import static com.planet.courier.constant.CourierConstant.FOLDER_PATH;
import static com.planet.courier.constant.CourierConstant.GRID_SIZE;
import static com.planet.courier.constant.CourierConstant.MAX_POOL_SIZE;
import static com.planet.courier.constant.CourierConstant.QUEUE_POOL_SIZE;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.springframework.core.io.ClassPathResource;

public class CourierUtil {

	public static void setParamsByFileSize() {
		try {
			Integer noOfLines = getNoOfLines(FOLDER_PATH.concat(FILE_NAME));
			if (noOfLines > 10000) {
				GRID_SIZE = 1000;
				CHUNK_SIZE = 50;
				MAX_POOL_SIZE = 1000;
				CORE_POOL_SIZE = 200;
				QUEUE_POOL_SIZE = 100;
			} else if (noOfLines > 1000) {
				GRID_SIZE = 100;
				CHUNK_SIZE = 10;
				MAX_POOL_SIZE = 1000;
				CORE_POOL_SIZE = 200;
				QUEUE_POOL_SIZE = 100;
			} else {
				GRID_SIZE = 10;
				CHUNK_SIZE = 2;
				MAX_POOL_SIZE = 10;
				CORE_POOL_SIZE = 2;
				QUEUE_POOL_SIZE = 2;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getNoOfLines(String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        LineNumberReader reader = new LineNumberReader(new FileReader(classPathResource.getFile().getAbsolutePath()));
        reader.skip(Integer.MAX_VALUE);
        return reader.getLineNumber();
    }
}
