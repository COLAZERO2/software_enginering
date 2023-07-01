package uk.ac.ucl.shell;

//escaping character to escape some keyword producing when command substitution 
public class Escape {
    public static String add(String toAdd) {
        String newString = "";
        if (toAdd.contains("'") || toAdd.contains("\"") || toAdd.contains("\\") || toAdd.contains("`")
                || toAdd.contains(";") || toAdd.contains("|") || toAdd.contains("*")) {
            for (int c = 0; c < toAdd.length(); c++) {
                if (toAdd.charAt(c) == '\'' || toAdd.charAt(c) == '"' || toAdd.charAt(c) == '\\'
                        || toAdd.charAt(c) == '`' || toAdd.charAt(c) == ';' || toAdd.charAt(c) == '|'
                        || toAdd.charAt(c) == '*') {
                    newString += "\\" + toAdd.charAt(c);
                } else {
                    newString += toAdd.charAt(c);
                }
            }
        } else {
            newString = toAdd;
        }
        return newString;
    }

    public static String ignore(String toIgnore) {
        String noEsc = "";
        boolean BACKSLASH_DISABLE = false;

        for (int c = 0; c < toIgnore.length(); c++) {
            if (!(toIgnore.charAt(c) == '\\')
                    || c > 0 && toIgnore.charAt(c) == '\\' && toIgnore.charAt(c - 1) == '\\' && !BACKSLASH_DISABLE
                    || c > 0 && toIgnore.charAt(c) == '\\' && toIgnore.charAt(c - 1) == '\\') {
                if (c > 0 && toIgnore.charAt(c) == '\\' && toIgnore.charAt(c - 1) == '\\' && !BACKSLASH_DISABLE) {
                    BACKSLASH_DISABLE = true;
                } else if (c > 0 && toIgnore.charAt(c) == '\\' && toIgnore.charAt(c - 1) == '\\') {
                    BACKSLASH_DISABLE = false;
                    continue;
                } else {
                    BACKSLASH_DISABLE = false;
                }
                noEsc += toIgnore.charAt(c);

            }
        }
        return noEsc;
    }
}
