package druid.util;

public class JavaUtil {

    //--------------------------------------------------------------------------

    public static String lcJavaName(String str) {
        if (str.length() > 0) {
            String[] words = str.split("_");
            StringBuffer javaName = new StringBuffer(str.length());
            javaName.append(words[0].substring(0,1).toLowerCase()).append(words[0].substring(1));
            for (int i = 1; i < words.length; i++) {
                javaName.append(words[i].substring(0,1).toUpperCase()).append(words[i].substring(1));
            }
            return javaName.toString();
        }
        return str;
    }

    //  --------------------------------------------------------------------------

    public static String ucJavaName(String str) {
        if (str.length() > 0) {
            String[] words = str.split("_");
            StringBuffer javaName = new StringBuffer(str.length());
            for (int i = 0; i < words.length; i++) {
                javaName.append(words[i].substring(0,1).toUpperCase()).append(words[i].substring(1));
            }
            return javaName.toString();
        }
        return str;
    }
}
