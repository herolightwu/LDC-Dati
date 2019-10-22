package lv.epasaule.ldcdati.json;


import android.text.Html;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lv.epasaule.ldcdati.adapter.ResultRow;

public class JsonParser {

    private static final String TAG = JsonParser.class.getSimpleName();

    private static final Map<String, String> JSON_KEY_TO_ID_UI_TEXT;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("suga", "Suga:");
        map.put("vards", "Vārds:");
        map.put("dzimums", "Dzimums:");
        map.put("skirne", "Šķirne:");
        map.put("asiniba", "Asinība:");
        map.put("dzimdat", "Dzimšanas datums:");
        map.put("likvdat", "Likvidēšanas datums:");
        map.put("likviemesls", "Likvidēšanas iemesls:");
        map.put("ganampulks", "Ganāmpulks:");
        map.put("novietne", "Novietne:");
        map.put("numurs", "Identifikators(i):");
        map.put("pazudis", "Pazudis:");
        map.put("pazudisdetalas", "");
        map.put("ipasaspazimes", "Īpašas pazīmes:");
        map.put("apmaciba", "Apmācības veids:");
//      map.put("trakumspote", "Vakcīna pret trakmussērgu:");
        map.put("adrese", "Pašvaldība:");
//      map.put("neapmaksats", "Neapmaksāts:");
        JSON_KEY_TO_ID_UI_TEXT = Collections.unmodifiableMap(map);
    }

    private static final Set<String> JSON_KEY_TO_RED_UI_TEXT;

    static {
        Set<String> set = new HashSet<>();
        set.add("bistams");
        set.add("apmacitsuzbrukt");
        set.add("pazudis1");
        JSON_KEY_TO_RED_UI_TEXT = Collections.unmodifiableSet(set);
    }

    public static AnimalsDataHtml jsonToAnimalsData(String jsonStr) {

        int animalCount = 0;
        StringBuilder htmlText = new StringBuilder();

        try {
            JSONObject json = new JSONObject(jsonStr);
            if (json.has("error")) {
                htmlText.append("Kļūda: <b>").append(json.getString("error")).append("</b>");
                return new AnimalsDataHtml(animalCount, htmlText.toString());
            }

            JSONArray arr = json.getJSONArray("rows");
            animalCount = arr.length();
            for (int i = 0; i < animalCount; i++) {
                JSONObject obj = arr.getJSONObject(i);

                JSONArray aizliegums = obj.getJSONArray("aizliegums");

                if (i > 0) { // tukša rinda, lai atdalītu vairākus dzīvniekus
                    htmlText.append("<br>");
                }

                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Object val = obj.get(key);
                    String valStr;
                    if (val == null
                            || TextUtils.isEmpty(valStr = val.toString())
                            || valStr.equals("null")
                            || valStr.equals("-1")) {
                        continue;
                    }
                    if (JSON_KEY_TO_ID_UI_TEXT.containsKey(key)) {
                        // identifikatori
                        if (key.equals("numurs")) {
                            JSONArray numurs = obj.getJSONArray("numurs");
                            if (numurs.length() > 0) {
                                StringBuilder list = new StringBuilder();
                                for (int j = 0; j < numurs.length(); j++) {
                                    JSONObject obj2 = numurs.getJSONObject(j);
                                    list.append(obj2.get("ID")).append("<br>");
                                }
                                htmlText.append(JSON_KEY_TO_ID_UI_TEXT.get(key))
                                        .append("<b>").append("&nbsp;").append(list.toString()).append("</b>");
                            }
                        } else {
                            htmlText.append(JSON_KEY_TO_ID_UI_TEXT.get(key))
                                    .append("<b>").append("&nbsp;").append(valStr).append("</b>")
                                    .append("<br>");
                        }
                    } else if (JSON_KEY_TO_RED_UI_TEXT.contains(key)) {
                        // sarkanie pazinojumi
                        String dzivnBrid = "<font color=\"red\"><b>" + valStr + "</b></font><br>";
                        htmlText.append(dzivnBrid);
                    } else if (key.equals("aizliegums")) {

                    }
//                  } else {
//                      htmlText.append(key)
//                              .append(":").append("<b>").append("&nbsp;").append(valStr).append("</b>")
//                              .append("<br>");
//                  }
                }

                if (aizliegums.length() > 0) {
                    StringBuilder list = new StringBuilder();
                    for (int j = 0; j < aizliegums.length(); j++) {
                        JSONObject obj2 = aizliegums.getJSONObject(j);
                        String aizliegStr = "<font color=\"red\"><b>"
                                + obj2.get("aizlieg") + "</b></font>";
                        String aprakstsStr = "<font color=\"red\"><b>"
                                + obj2.get("apraksts") + "</b></font>";

                        list.append(aizliegStr).append(":&nbsp;").append(aprakstsStr).append("<br>");
                    }
                    htmlText.append(list);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new AnimalsDataHtml(animalCount, htmlText.toString());
    }

    public static AnimalsData jsonToResultRows(String jsonStr) {

        int animalCount = 0;
        List<ResultRow> resultRows = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(jsonStr);
            if (json.has("error")) {
                CharSequence rowKey = applyKeyStyle("Kļūda:");
                CharSequence rowValue = applyValueStyle(json.getString("error"));
                ResultRow resultRow = new ResultRow(rowKey, rowValue);
                return new AnimalsData(0, Collections.singletonList(resultRow));
            }

            JSONArray arr = json.getJSONArray("rows");
            animalCount = arr.length();
            for (int i = 0; i < animalCount; i++) {
                JSONObject obj = arr.getJSONObject(i);

                JSONArray aizliegums = obj.getJSONArray("aizliegums");

                if (i > 0) { // tukša rinda, lai atdalītu vairākus dzīvniekus
                    resultRows.add(ResultRow.EMPTY);
                }

                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Object val = obj.get(key);
                    String valStr;
                    if (val == null
                            || TextUtils.isEmpty(valStr = val.toString())
                            || valStr.equals("null")
                            || valStr.equals("-1")) {
                        continue;
                    }
                    if (JSON_KEY_TO_ID_UI_TEXT.containsKey(key)) {
                        // identifikatori
                        if (key.equals("numurs")) {
                            JSONArray numurs = obj.getJSONArray("numurs");
                            if (numurs.length() > 0) {
                                for (int j = 0; j < numurs.length(); j++) {
                                    CharSequence rowKey;
                                    if (j == 0) {
                                        rowKey = applyKeyStyle(JSON_KEY_TO_ID_UI_TEXT.get(key));
                                    } else {
                                        rowKey = "";
                                    }
                                    JSONObject obj2 = numurs.getJSONObject(j);
                                    CharSequence rowValue = applyValueStyle(obj2.get("ID").toString());
                                    resultRows.add(new ResultRow(rowKey, rowValue));
                                }
                            }
                        } else {
                            CharSequence rowKey = applyKeyStyle(JSON_KEY_TO_ID_UI_TEXT.get(key));
                            CharSequence rowValue = applyValueStyle(valStr);
                            resultRows.add(new ResultRow(rowKey, rowValue));
                        }
                    } else if (JSON_KEY_TO_RED_UI_TEXT.contains(key)) {
                        // sarkanie pazinojumi
                        CharSequence rowValue = applyWarningStyle(valStr);
                        resultRows.add(new ResultRow("", rowValue));
                    } else if (key.equals("aizliegums")) {

                    }
//                  } else {
//                      CharSequence rowKey = applyKeyStyle(key);
//                      CharSequence rowValue = applyValueStyle(valStr);
//                      resultRows.add(new ResultRow(rowKey, rowValue));
//                  }
                }

                if (aizliegums.length() > 0) {
                    for (int j = 0; j < aizliegums.length(); j++) {
                        JSONObject obj2 = aizliegums.getJSONObject(j);
                        CharSequence rowKey = applyForbiddedStyle(obj2.get("aizlieg").toString());
                        CharSequence rowValue = applyForbiddedStyle(obj2.get("apraksts").toString());
                        resultRows.add(new ResultRow(rowKey, rowValue));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new AnimalsData(animalCount, resultRows);
    }

    private static CharSequence applyKeyStyle(String value) {
        return Html.fromHtml("<font color=\"#b1b1b1\">" + value + "</font>");
    }

    private static CharSequence applyValueStyle(String value) {
        return Html.fromHtml("<font color=\"#545766\">" + value + "</font>");
    }

    private static CharSequence applyWarningStyle(String value) {
        return Html.fromHtml("<font color=\"red\"><b>" + value + "</b></font>");
    }

    private static CharSequence applyForbiddedStyle(String value) {
        return Html.fromHtml("<font color=\"red\"><b>" + value + "</b></font>");
    }

    public static class AnimalsDataHtml {

        private int counter;
        private String htmlText;

        public AnimalsDataHtml(int counter, String htmlText) {
            this.counter = counter;
            this.htmlText = htmlText;
        }

        public int counter() {
            return counter;
        }

        public String htmlText() {
            return htmlText;
        }
    }

    public static class AnimalsData {

        private int counter;
        private List<ResultRow> results;

        public AnimalsData(int counter, List<ResultRow> results) {
            this.counter = counter;
            this.results = results;
        }

        public int counter() {
            return counter;
        }

        public List<ResultRow> results() {
            return results;
        }
    }

}

// --------------------------WEB SOURCE---------------------------------------------------------
/*
    jQuery(function($){
        var lang = "lv",
                dziv_hint = '',
                maj_dziv_hint = '',
                auth_hint = '';

        var nosaukumi = {suga:"Suga:", vards:"Vārds:", dzimums:"Dzimums:", skirne:"Šķirne:", asiniba:"Asinība:", dzimdat:"Dzimšanas datums:", likvdat:"Likvidēšanas datums:", likviemesls:"Likvidēšanas iemesls:", ganampulks:"Ganāmpulks:",
                novietne:"Novietne:", */
/*aizl*//*
 numurs:"Identifikators(i):", pazudis:"Pazudis:", pazudisdetalas:"", ipasaspazimes:"Īpašas pazīmes:", apmaciba:"Apmācības veids:", trakumspote:"Vakcīna pret trakmussērgu:",
                adrese:"Pašvaldība:"};

        function pad(str,max) {
            return str.length < max ? pad("0" + str, max) : str;
        };
        $( "#ValidNumber" ).blur(function() {
            var dziv = $(this).val();
            if(dziv.length != 12 || dziv.charAt(0)!='0') return;
            var rx = RegExp('^([0-9]{12})$');
            var tmp_dziv = pad(dziv,12);
            rx.test(tmp_dziv) ? dziv = 'LV'+tmp_dziv : dziv;
            $(this).val(dziv);
        })

        $("#btn_meklet").click(function() {
            $.post("/ajax/w_ajax_lv.php", {
                    "dzid": $("#ValidNumber").val(),
                    "auth_code": $("#auth_code").val(),
                    "tips": "pub_dziv",
                    "c_param": "dziv",
                    "lang": lang
            },
            function(data){

                $("#t188 tr").remove();
                //$("#t188").children().remove()
                if (typeof data.error !== 'undefined') {
                    //if(data.error.length > 0){
                    var date = new Date();
                    $("#captcha").attr("src","/ajax/captcha_image.php?src=dziv&"+date.getTime());
                    $("#auth_code").val('');
                    $('#t188').fadeOut('fast');
                    $('#err1').fadeOut('fast',function() {

                        document.getElementById("t188").style.display="none";
                        document.getElementById("err1").style.display="none";
                        document.getElementById("err1").innerHTML = data.error;//add error
                        $('#err1').fadeIn('fast');//show error
                        $('#ValidNumber').focus();
                    });
                }else{
                    var date = new Date();
                    $("#captcha").attr("src","/ajax/captcha_image.php?src=dziv&"+date.getTime());
                    $("#auth_code").val('');

                    var num = 1;

                    $('#err1').fadeOut('fast');
                    $('#t188').fadeOut('fast',function() {

                        $.each(data.rows, function(index,item) {
                            //for (i = 0; i < data.rows.length; i++) {

                            var result = [];
                            result = item.aizliegums;

                            var iden = [];
                            iden = item.numurs;

                            if (num>1)//tukša rinda, lai atdalītu vairākus dzīvniekus
                                $('#t188').append('<tr style="height:50px;"><td class="t191" style="background-color:white!important">&nbsp</td><td class="t192" style="background-color:white!important" id="empty'+num+'" name="empty'+num+'">&nbsp</td></tr>')

                            var dzivn_brid = '';
                            $.each(item, function(index,item2) {
                                //if (Object.keys(item2).length>0){
                                if (item2.length>0){
                                    if (typeof nosaukumi[index] !== 'undefined'){
                                        //identifikatori
                                        if(index == 'numurs'){
                                            if(iden!=''){
                                                var list = '';
                                                $.each(iden, function(index,item3) {
                                                    list += item3.ID+'<br>';
                                                });
                                                $('#t188').append('<tr><td class="t191">'+nosaukumi[index]+'</td><td class="t192" id="l-'+nosaukumi[index]+''+num+'" name="l-'+nosaukumi[index]+''+num+'">'+list+'</td></tr>');
                                            }
                                        }else
                                            $('#t188').append('<tr><td class="t191">'+nosaukumi[index]+'</td><td class="t192" id="l-'+nosaukumi[index]+''+num+'" name="l-'+nosaukumi[index]+''+num+'">'+item2+'</td></tr>')
                                    };
                                    //sarkanie pazinojumi
                                    if(index == 'bistams'){
                                        dzivn_brid +='<tr><td colspan = 2 class="t192" style="color:red; text-align:center;" id="l-bistams'+num+'" name="l-bistams'+num+'"><b>'+item2+'</b></td></tr>';
                                        $('#t188').append(dzivn_brid);
                                    };
                                    if(index == 'apmacitsuzbrukt'){
                                        dzivn_brid +='<tr><td colspan = 2 class="t192" style="color:red; text-align:center;" id="l-uzbrukt'+num+'" name="l-uzbrukt'+num+'"><b>'+item2+'</b></td></tr>';
                                        $('#t188').append(dzivn_brid);
                                    };
                                    if(index == 'pazudis1'){
                                        dzivn_brid +='<tr><td colspan = 2 class="t192" style="color:red; text-align:center;" id="l-uzbrukt'+num+'" name="l-uzbrukt'+num+'"><b>'+item2+'</b></td></tr>';
                                        $('#t188').append(dzivn_brid);
                                    };

                                };
                            });
                            //$('#t188').append(dzivn_brid);

                            document.getElementById("t188").style.display="none";
                            document.getElementById("err1").style.display="none";

                            if(result!='')
                            {
                                var table= '<tr><table  style="border-collapse:collapse";>';

                                $.each(result,function(i,element){

                                table +='<tr>';
                                table +='<td class="t192a" style="color:red;"><b>'+ element.aizlieg + '</b></td>';
                                table +='<td style="color:red;"><b>'+ element.apraksts + '</b></td>';
                                table +='</tr>';
                            });
                                table +='</table></tr>';
                                $('#t188').append(table);
                            }


                            $('#t188').fadeIn('fast');
                            $('#ValidNumber').focus();
                            num++;
                        });//each cikls //)
                    });//fade
                }
            }, "json");
        });

        $('#ValidNumber')
                .keypress(function(e) {
            var key = e.charCode ? e.charCode : e.keyCode ? e.keyCode : 0;
            if (key != 13) return;
            $(this).blur();
            $("#auth_code").focus();
        });


        $("#auth_code")
                .keypress(function(e) {
            var key = e.charCode ? e.charCode : e.keyCode ? e.keyCode : 0;
            if (key != 13) return;
            $("#btn_meklet").trigger("click");
        });

    });

    function getTodaysDate(){
        var today = new Date();
        var dd = today.getDate();
        var mm = today.getMonth()+1; //January is 0!

        var yyyy = today.getFullYear();
        if(dd<10){dd='0'+dd} if(mm<10){mm='0'+mm} var today = dd+'.'+mm+'.'+yyyy;
        document.getElementById('todaysDate').innerHTML = today;
    }
*/
