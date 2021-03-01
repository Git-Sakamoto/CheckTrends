package com.example.checktrends.wikipedia;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.checktrends.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    private Context context;
    private Handler handler;
    private ProgressDialog dialog;
    private String URL = "https://ja.wikipedia.org/w/api.php?format=json&utf8&action=query&prop=revisions&rvprop=content&titles=";

    HttpRequest(Context context,String date){
        this.context = context;
        URL = URL + date;
    }

    void execute(){
        handler = new Handler(Looper.getMainLooper());

        dialog = new ProgressDialog(context);
        dialog.setTitle("読み込み中");
        dialog.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(context, R.string.error_message_is_cannot_connect,Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                WikipediaContent content = getWikipediaContent(response.body().string());
                handler.post(() -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    loadingComplete(content);
                });
            }
        });
    }

    WikipediaContent getWikipediaContent(String response){
        WikipediaContent result = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject pagesObject = jsonObject.getJSONObject("query").getJSONObject("pages");
            JSONObject pageNumberObject = pagesObject.getJSONObject(pagesObject.keys().next());
            String content = pageNumberObject.getJSONArray("revisions").getJSONObject(0).getString("*");

            String[] events = getEvents(content);
            String[] birthdays = getBirthdays(content);
            String[] anniversaries = getAnniversaries(content);

            result = new WikipediaContent(events,birthdays,anniversaries);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    String[] getEvents(String content){
        String[]events = replaceContent(content,"== できごと ==");

        //不要な改行コードを削除
        List<String>trim = new ArrayList<>();
        for(int i = 0; i < events.length; i++){
            if(!events[i].equals("")){
                trim.add(events[i]);
            }
        }
        Collections.reverse(trim);

        return trim.toArray(new String[trim.size()]);
    }

    String[] getBirthdays(String content){
        String[] birthdays = replaceContent(content,"== 誕生日 ==");

        //不要な改行コードを削除
        List<String>trim = new ArrayList<>();
        for(int i = 0; i < birthdays.length; i++){
            if(!birthdays[i].equals("")){
                trim.add(birthdays[i]);
            }
        }
        Collections.reverse(trim);

        return trim.toArray(new String[trim.size()]);
    }

    String[] getAnniversaries(String content){
        String[] anniversaries = replaceContent(content,"== 記念日・年中行事 ==");

        //内容の結合作業
        //結果は改行コードで区切る形で配列に格納しているが、記念日・年中行事名「～の日」と、それに対する説明文の間には、改行コードが挿入されている
        //結果的に記念日・年中行事名と説明文が別々に格納されてしまうため、文字列の結合作業を要する
        List<String>linking = new ArrayList<>();
        for(int i = 0; i < anniversaries.length-1;i++){
            if(anniversaries[i + 1].matches(":.*")){
                linking.add(anniversaries[i] + anniversaries[i + 1]);
                i++;
            }else if(!anniversaries[i].equals("")){
                linking.add(anniversaries[i]);
            }
        }
        anniversaries = linking.toArray(new String[linking.size()]);

        //ISO639-2コードを国名として表示する
        //Wikipediaのページに記載されている記念日・年中行事名「～の日（）」の（）内は、国名が表示されている
        //しかしJSONでデータを取得するとISO639-2コード「3桁の英字　例：日本 = JPN」になっており、日本語の国名で表記するために変換処理を行う
        Map<String, String> countries = getCountries();
        for(int i = 0; i < anniversaries.length; i++){
            String anniversary = anniversaries[i];

            //ISO639-2コードを取得
            String regex = "（([A-Za-z]*).*）";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(anniversary);
            String iso3;
            if (matcher.find()){
                iso3 = matcher.group(1);
            }else{
                iso3 = "不明";
            }

            try{
                String countryName = countries.get(iso3);
                anniversaries[i] = anniversary.replace(iso3,countryName);
            }catch (NullPointerException e){
                //～の日（国名）の（）内がISO639-2コードではない
                e.printStackTrace();
            }
        }
        return anniversaries;
    }

    //onResponse内の変数responseで一括した処理が理想だけど、エラーが出るので個別対応で保留
    String[] replaceContent(String content,String title){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(content);
        stringBuilder.delete(0,content.indexOf("*" , content.indexOf(title)));
        String str = stringBuilder.toString();
        String[]array = str.substring(0,str.indexOf("=="))
                .replaceAll("\\[[^\\]]*\\|","")
                .replaceAll("\\{\\{.*?\\|","")
                .replaceAll("\\|.*\\|.+?\\}\\}","")
                .replaceAll("<.*>","")
                .replaceAll("\\{\\{","")
                .replaceAll("\\}\\}","")
                .replace("*","")
                .replace("[", "")
                .replace("]", "")
                .split("\n");
        return array;
    }

    private Map<String, String> getCountries() {
        Map<String, String> countries = new HashMap<>();
        String[] isoCountries = Locale.getISOCountries();
        for (String iso2 : isoCountries) {
            Locale locale = new Locale("jp", iso2);
            String iso3 = locale.getISO3Country();
            String name = locale.getDisplayCountry();

            if (!"".equals(iso3) && !"".equals(name)) {
                countries.put(iso3, name);
            }
        }
        countries.put("World","世界");
        return countries;
    }

    void loadingComplete(WikipediaContent content){}

}
