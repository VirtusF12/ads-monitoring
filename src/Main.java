
/*
   https://oauth.vk.com/authorize?client_id=5490057&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.52
*/

//import CoreVkApiParse.AdsWall.AdsPost;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        try {
            final String strIdPublic = "-101827016";
            final String verVKApi = "5.85";
            final String strCountPost = "30";
            final String accessToken = "76f2dfdfaa0fed1be88bde0c70379463afdf490509ed639b126097a103e761119a85fdb37087fe28d436e";
            final String url = "https://api.vk.com/method/wall.get?owner_id="+strIdPublic+"&count="+strCountPost+"&offset=0&access_token="+accessToken+"&v="+verVKApi;

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String jsonString = response.toString();

            Object objParse = new JSONParser().parse(jsonString); // Считываем json
            JSONObject jsonObj = (JSONObject) objParse;

            JSONObject ja =  (JSONObject) jsonObj.get("response");
            JSONArray jaItems = (JSONArray) ja.get("items");
            List<AdsPost> adsPosts = new ArrayList<>();

            for (int i = 0; i < jaItems.size(); i++){

                boolean isAds = false, isAttachment = false,
                        isAttachmentPhoto = false, isAttachmentLink = false,
                        isCopyHistory = false, isCopyHistoryPhoto = false, isCopyHistoryVideo = false,
                        isCopyHistoryAtt = false;
                String glTypeAt = "", glTextAt = "", glTypeCh = "", glTextCh = "";
                ArrayList<String> glPhotosAt = new ArrayList<>();
                ArrayList<String> glVideosAt = new ArrayList<>();
                ArrayList<String> glLinksAt = new ArrayList<>();
                ArrayList<String> glPhotosCh = new ArrayList<>();
                ArrayList<String> glVideosCh = new ArrayList<>();
                ArrayList<String> glLinksCh= new ArrayList<>();
                JSONObject joTemp = (JSONObject) jaItems.get(i);

                String marked_as_ads = "\n\n\nРЕКЛАМА: " + joTemp.get("marked_as_ads").toString();
                String post_type = "ТИП: " + joTemp.get("post_type").toString();
                String text = "ТЕКСТ: " + joTemp.get("text").toString();
                // System.out.println(marked_as_ads+"\n"+post_type+"\n"+text); // вывод
                glTypeAt = post_type;
                glTextAt = text;

                if (marked_as_ads.equals("1"))
                    isAds = true;

                try {
                    JSONArray jaTempAttachment = (JSONArray) joTemp.get("attachments");

                    for (int indexObj = 0; indexObj < jaTempAttachment.size(); indexObj++) { // выполнить перебор по всем объектам списка attachment
                        JSONObject joTempAttPh = (JSONObject) jaTempAttachment.get(indexObj);
                        String currentType = joTempAttPh.get("type").toString(); // получение типа photo video link

                        switch (currentType) {

                            case "photo":
                                JSONObject joTempAttPhoto = (JSONObject) joTempAttPh.get("photo");
                                JSONArray jaTempAttPhotoSize = (JSONArray) joTempAttPhoto.get("sizes");

                                for (int j = 0; j < jaTempAttPhotoSize.size(); j++) { // получение из attachments первого фото "q" типа
                                    JSONObject joSize = (JSONObject) jaTempAttPhotoSize.get(j);
                                    if (joSize.get("type").toString().equals("q")) {
                                        // System.out.println("ССЫЛКА НА ФОТО: " + joSize.get("url").toString() + "\nШИРИНА: " + joSize.get("width").toString() + "\nВЫСОТА: " + joSize.get("height").toString());
                                        glPhotosAt.add("ССЫЛКА НА ФОТО: " + joSize.get("url").toString());
                                        break;
                                    }
                                }
                                isAttachmentPhoto = true;
                                ;break;
                            case "video": ;break;
                            case "link":
                                JSONObject joTempAttLink = (JSONObject) joTempAttPh.get("link");
                                String urlLink = "ССЫЛКА: " + joTempAttLink.get("url").toString();
                                String titleLink = "НАЗВАНИЕ: " + joTempAttLink.get("title").toString();
                                String descriptionLink = "ОПИСАНИЕ: " + joTempAttLink.get("description").toString();
                                // System.out.println(urlLink + "\n" + titleLink + "\n" + descriptionLink + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
                                glLinksAt.add(urlLink + "\n" + titleLink + "\n" + descriptionLink + "\n");
                                isAttachmentLink = true;
                                ;break;
                            default:
                                System.out.println("--- объектов в attachment не обнаружено");
                                ;break;
                        }
                    } // завершение цикла перебора объектов attachment
                    isAttachment = true;

                } catch (Exception ex) {
                    System.out.println("нет attachment");
                    isAttachment = false;
                }
                ///////////////////////////////////////////////////////////////////////////////////////
                try { // репост в группе
                    JSONArray jaTempCopyHistory = (JSONArray) joTemp.get("copy_history");

                    for (int indexCH = 0; indexCH < jaTempCopyHistory.size(); indexCH++) { // список copy_history

                        JSONObject joCopyHistory = (JSONObject) jaTempCopyHistory.get(indexCH);
                        String post_typeCopyHistory = "ТИП CopyHistory: " + joCopyHistory.get("post_type").toString();
                        String textCopyHistory = "ТЕКСТ CopyHistory: " + joCopyHistory.get("text").toString();
                        // System.out.println(post_typeCopyHistory+"\n"+textCopyHistory+"\n"); // вывод
                        glTypeCh = post_typeCopyHistory;
                        glTextCh = textCopyHistory;

                        JSONArray jaAttCopyHistoryIn = (JSONArray) joCopyHistory.get("attachments");

                        // begin
                        for (int indexObj = 0; indexObj < jaAttCopyHistoryIn.size(); indexObj++) { // выполнить перебор по всем объектам списка attachment
                            JSONObject joTempAttPh = (JSONObject) jaAttCopyHistoryIn.get(indexObj);
                            String currentType = joTempAttPh.get("type").toString(); // получение типа photo video link

                            switch (currentType) {

                                case "photo":
                                    JSONObject joTempAttPhoto = (JSONObject) joTempAttPh.get("photo");
                                    JSONArray jaTempAttPhotoSize = (JSONArray) joTempAttPhoto.get("sizes");

                                    for (int j = 0; j < jaTempAttPhotoSize.size(); j++) { // получение из attachments первого фото "q" типа
                                        JSONObject joSize = (JSONObject) jaTempAttPhotoSize.get(j);
                                        if (joSize.get("type").toString().equals("q")) {
                                            // System.out.println("ССЫЛКА НА ФОТО: " + joSize.get("url").toString() + "\nШИРИНА: " + joSize.get("width").toString() + "\nВЫСОТА: " + joSize.get("height").toString());
                                            glPhotosCh.add("ССЫЛКА НА ФОТО: " + joSize.get("url").toString());
                                            // System.out.println("###########PHOTO ADD");
                                            break;
                                        }
                                    }
                                    isCopyHistoryPhoto = true;
                                    ;break;
                                case "video":
                                    isCopyHistoryVideo = true;
                                    ;break;
                                case "link":
                                    JSONObject joTempAttLink = (JSONObject) joTempAttPh.get("link");
                                    String urlLink = "ССЫЛКА: " + joTempAttLink.get("url").toString();
                                    String titleLink = "НАЗВАНИЕ: " + joTempAttLink.get("title").toString();
                                    String descriptionLink = "ОПИСАНИЕ: " + joTempAttLink.get("description").toString();
                                    // System.out.println(urlLink + "\n" + titleLink + "\n" + descriptionLink + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
                                    glLinksCh.add(urlLink + "\n" + titleLink + "\n" + descriptionLink + "\n~~~");
                                    ;break;
                                default:
                                    System.out.println("--- объектов в attachment не обнаружено");
                                    ;break;
                            }
                        } // завершение цикла перебора объектов attachment
                        // end




                    } // завершение цикла просмотра списка групп
                    isCopyHistory = true;

                } catch (Exception ex){
                    // System.out.println("нет copy_history");
                    isCopyHistory = false;
                }


                if (isCopyHistory && isCopyHistoryPhoto) { // репост и фотки в нем

                    adsPosts.add(new AdsPost(glTypeAt, (!glTextCh.equals("") ? glTextCh : glTextAt),
                            (glPhotosCh.size()>0?glPhotosCh:glVideosAt),
                            (glVideosCh.size()>0?glVideosCh:glVideosAt),
                            (glLinksCh.size()>0?glLinksCh:glLinksAt)
                    ));
                    adsPosts.get(adsPosts.size()-1).showAdsPost();

                } else if (isAttachment && isAttachmentPhoto && isAttachmentLink) { // пост и ссылка

                    adsPosts.add(new AdsPost(glTypeAt, (!glTextCh.equals("") ? glTextCh : glTextAt),
                            (glVideosAt.size()>0?glVideosAt:glPhotosCh),
                            (glVideosAt.size()>0?glVideosAt:glVideosCh),
                            (glLinksAt.size()>0?glLinksAt:glLinksCh)
                    ));
                    adsPosts.get(adsPosts.size()-1).showAdsPost();

                }
            }
        }   catch (IOException ex){
            System.out.println("error: IOException");
        }   catch (ParseException e) {
            System.out.println("error: ParseException");
        }   catch(Exception ex) {
            System.out.println("error: Exception");
        }

    }
}

