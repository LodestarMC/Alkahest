package team.lodestar.alkahest.core.render;

public class StringHelper {
    public static String capitalizeFirstLetter(String pString) {
        if(pString.length() > 0){
            return pString.substring(0, 1).toUpperCase() + pString.substring(1);
        }
        return pString;
    }

    public static String capitalizeWords(String pString) {
        String[] words = pString.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String word : words){
            sb.append(capitalizeFirstLetter(word)).append(" ");
        }
        return sb.toString().trim();
    }
}
