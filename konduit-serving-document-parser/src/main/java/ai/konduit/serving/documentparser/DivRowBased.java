package ai.konduit.serving.documentparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Extract based on divs where
 * the previous div's entry is
 * the name and the div below is the value.
 *
 */
public class DivRowBased implements TableExtractor {
    private String rowSelector;
    private List<String> fieldNames;
    private List<String> partialFieldNames;
    private Map<String,List<String>> tableSpecificFields;
    private Map<String,TextExtractionType> textExtractionTypes;

    public DivRowBased(String rowSelector, List<String> fieldNames,List<String> partialFieldNames,Map<String,List<String>> tableSpecificFields,Map<String,TextExtractionType> textExtractionTypes) {
        this.rowSelector = rowSelector;
        this.fieldNames = fieldNames;
        this.partialFieldNames = partialFieldNames;
        this.tableSpecificFields = tableSpecificFields;
        this.textExtractionTypes = textExtractionTypes;


    }

    @Override
    public Map<String, Map<String, List<String>>> extract(String html, List<String> tableSeparators) {
        Map<String,Map<String,List<String>>> ret = new LinkedHashMap<>();

        Map<String,List<String>> currTable = new LinkedHashMap<>();
        Document parse = Jsoup.parse(html);
        Elements select = parse.select(rowSelector);
        StringBuilder fieldValue = new StringBuilder();
        String currFieldName = null;
        String currTableName = null;
        for(int i = 0; i < select.size(); i++) {
            Element element = select.get(i);
            String baseText = getTextExtractionType(currFieldName) == TextExtractionType.TEXT ? element.text() : element.html();
            String elementText = baseText.replace("&nbsp;"," ").replace("NBSP"," ");
            if(tableSeparators.contains(elementText)) {
                if(!currTable.isEmpty())
                    ret.put(currTableName,currTable);

                currTableName = elementText;
                addValueToList(currTable, fieldValue, currFieldName);


                currTable = new LinkedHashMap<>();

                currFieldName = null;
                fieldValue = new StringBuilder();
                continue;
            }

            //check both the global and table specific fiields
            if(fieldNames.contains(elementText) || currTableName != null && tableSpecificFields.containsKey(currTableName) && tableSpecificFields
                    .get(currTableName).contains(elementText)) {
                //first encountered field
                if(currFieldName == null) {
                    currFieldName = elementText;
                }  else { //next field
                    addValueToList(currTable, fieldValue, currFieldName);


                    fieldValue = new StringBuilder();
                    currFieldName = elementText;
                }
            } else if(currFieldName != null) { //currently in a key just append text
                fieldValue.append(elementText + " ");
            } else if(partialFieldNames != null && !partialFieldNames.isEmpty()) {
                for(String partialFieldName : partialFieldNames) {
                    if(elementText.contains(partialFieldName)) {
                        List<String> values;
                        if(currTable.containsKey(partialFieldName)) {
                            values = currTable.get(partialFieldName);
                        } else {
                            values = new ArrayList<>();
                            currTable.put(partialFieldName,values);
                        }

                        values.add(elementText.replace(partialFieldName,""));
                    }
                }
            }

            //add value at the end if one exists
            if(i == select.size() - 1 && currFieldName != null && !fieldValue.toString().isEmpty()) {
                addValueToList(currTable,fieldValue,currFieldName);
            }

        }



        //after everything is done, add last table if it's not empty
        if(!currTable.isEmpty()) {
            ret.put(currTableName,currTable);
        }

        return ret;
    }

    private TextExtractionType getTextExtractionType(String currentFieldName) {
        if(textExtractionTypes == null || textExtractionTypes.isEmpty() || currentFieldName == null)
            return TextExtractionType.TEXT;
        else {
            return textExtractionTypes.getOrDefault(currentFieldName,TextExtractionType.TEXT);
        }
    }

    private void addValueToList(Map<String, List<String>> currTable, StringBuilder fieldValue, String currFieldName) {
        List<String> valueList;
        if(currTable.containsKey(currFieldName)) {
            valueList = currTable.get(currFieldName);
        } else {
            valueList = new ArrayList<>();
        }

        valueList.add(fieldValue.toString().trim());
        currTable.put(currFieldName,valueList);
    }
}
