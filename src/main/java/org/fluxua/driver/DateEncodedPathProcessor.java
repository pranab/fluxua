/*
 * Fluxua: A simple Hadoop map reduce workflow engine
 * Author: Pranab Ghosh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.fluxua.driver;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author pranab
 */
public class DateEncodedPathProcessor extends PathProcessor {
    private String date;
    private int timeInterval;
    
    public  void initialize(Map<String, String> inputProcessorArgMap) {
       timeUnit =  inputProcessorArgMap.get("timeUnit");
       timeInterval =  Integer.parseInt(inputProcessorArgMap.get("timeUnit"));
    }   

    public DateEncodedPathProcessor() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));   
        if (timeUnit.equals("hour")){
            cal.add(Calendar.HOUR_OF_DAY, - timeInterval);
        } else {
            cal.add(Calendar.DAY_OF_MONTH, -timeInterval);
        }
        Format formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        date = formatter.format(cal.getTime());
    }
    
    

    @Override
    public String processInputPath(String inputPath) {
        if (timeUnit.equals("hour")){
            inputPath = inputPath.replace("{date}", date);
        } else {
            String dateWc = date.substring(0, date.length()-2) + "*";
            inputPath = inputPath.replace("{date}", dateWc);
        }
        return inputPath;
    }

    @Override
    public String processOutputPath(String outputPath) {
        if (timeUnit.equals("hour")){
            outputPath = outputPath.replace("{date}", date);
        } else {
            String dateWoHour = date.substring(0, date.length()-2);
            outputPath = outputPath.replace("{date}", dateWoHour);
        }
        return outputPath;
    }
}
