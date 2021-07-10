import java.util.ArrayList;
import java.util.List;

public class AdsPost {

        private String post_type, text;
        private List<String> photos, videos, links;

        public AdsPost(String post_type, String text, ArrayList<String> photos, ArrayList<String> videos, ArrayList<String> links) {

            this.post_type = post_type;
            this.text = text;

            this.photos = photos;
            this.videos = videos;
            this.links = links;
        }

        public String getPostType() { // +
            return this.post_type;
        }

        public String getPostText() { // +
            return this.text;
        }

        public String getImgIndex(int index) { // +
            return this.photos.get(index);
        }



        public String showAdsPost() {

            //System.out.println(post_type);
            //System.out.println(text);
            String res = post_type + "\n" + text + "\n";
            if (photos.size()>0){
                String strPhotos = "~~~PHOTO~~~";

                for (int i = 0; i < photos.size(); i++)
                    strPhotos += photos.get(i)+"\n";


                res += strPhotos;
                // System.out.println(strPhotos);
            }

            // if (videos.size()>0)
            if (links.size()>0){
                String strLinks = "";
                for(int i=0; i < links.size(); i++)
                    strLinks += links.get(i)+"\n";
                // System.out.println(strLinks);
                res += strLinks;
            }
            return res;

        }



}
