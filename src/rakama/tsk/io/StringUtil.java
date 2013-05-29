/*
 * Copyright (c) 2012, RamsesA <ramsesakama@gmail.com>
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package rakama.tsk.io;

public class StringUtil
{
    public static boolean isAlphaNumeric(String str)
    {
        char[] characters = str.toCharArray();

        for(char c : characters)
            if(!Character.isLetterOrDigit(c))
                return false;

        return true;
    }

    public static boolean isAlphaNumeric(String str, boolean allowSpaces)
    {
        char[] characters = str.toCharArray();

        for(char c : characters)
            if(!Character.isLetterOrDigit(c) && !(allowSpaces && Character.isSpaceChar(c)))
                return false;

        return true;
    }

    public static String applyTitleCase(String name)
    {
        if(name.length() > 1)
        {
            name = name.toLowerCase();
            name = Character.toTitleCase(name.charAt(0)) + name.substring(1);
        }
        else
            name = name.toUpperCase();

        return name;
    }
}