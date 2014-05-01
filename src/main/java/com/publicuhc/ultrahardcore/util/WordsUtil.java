/*
 * WordsUtil.java
 *
 * Copyright (c) 2014 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * This file is part of UltraHardcore.
 *
 * UltraHardcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraHardcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraHardcore.  If not, see <http ://www.gnu.org/licenses/>.
 */

package com.publicuhc.ultrahardcore.util;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordsUtil {

    private final List<String> m_adjectives;

    private final List<String> m_nouns;

    private final Random m_random = new Random();

    private static final Pattern LENGTH_PATTERN = Pattern.compile(
            "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
            Pattern.CASE_INSENSITIVE);

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    private static final long MILLIS_PER_HOUR   = MILLIS_PER_MINUTE * 60;
    private static final long MILLIS_PER_DAY    = MILLIS_PER_HOUR * 24;
    private static final long MILLIS_PER_WEEK   = MILLIS_PER_DAY * 7;
    private static final long MILLIS_PER_MONTH  = MILLIS_PER_DAY * 30;
    private static final long MILLIS_PER_YEAR   = MILLIS_PER_DAY * 365;

    /**
     * @param manager the config manager
     */
    @Inject
    private WordsUtil(Configurator manager){
        FileConfiguration config = manager.getConfig("words");
        m_adjectives = config.getStringList("adjectives");
        m_nouns = config.getStringList("nouns");
    }

    /**
     * @return a random team name
     */
    public String getRandomTeamName() {
        return "The " + m_adjectives.get(m_random.nextInt(m_adjectives.size())) + " " + m_nouns.get(m_random.nextInt(m_nouns.size()));
    }

    /**
     * Turns human readable config times into milliseconds
     * @param time the string
     * @return the amount of millis
     */
    public static long parseTime(String time) {
        if("infinite".equalsIgnoreCase(time)){
            return Long.MAX_VALUE/2;
        }
        long duration = 0;
        boolean match = false;
        Matcher mat = LENGTH_PATTERN.matcher(time);
        while (mat.find())    {
            if (mat.group() != null && !mat.group().isEmpty()) {
                for (int i = 0; i < mat.groupCount(); i++) {
                    if (mat.group(i) != null && !mat.group(i).isEmpty()) {
                        match = true;
                        break;
                    }
                }
                if (match){
                    if (mat.group(1) != null && !mat.group(1).isEmpty()){
                        duration += MILLIS_PER_YEAR * Integer.parseInt(mat.group(1));
                    }
                    if (mat.group(2) != null && !mat.group(2).isEmpty()){
                        duration += MILLIS_PER_MONTH * Integer.parseInt(mat.group(2));
                    }
                    if (mat.group(3) != null && !mat.group(3).isEmpty()){
                        duration += MILLIS_PER_WEEK * Integer.parseInt(mat.group(3));
                    }
                    if (mat.group(4) != null && !mat.group(4).isEmpty()){
                        duration += MILLIS_PER_DAY * Integer.parseInt(mat.group(4));
                    }
                    if (mat.group(5) != null && !mat.group(5).isEmpty()){
                        duration += MILLIS_PER_HOUR * Integer.parseInt(mat.group(5));
                    }
                    if (mat.group(6) != null && !mat.group(6).isEmpty()){
                        duration += MILLIS_PER_MINUTE * Integer.parseInt(mat.group(6));
                    }
                    if (mat.group(7) != null && !mat.group(7).isEmpty()){
                         duration+= MILLIS_PER_SECOND * Integer.parseInt(mat.group(7));
                    }
                    break;
                }
            }
        }
        return duration;
    }

    /**
     * Format into human readable time left
     * @param timeUnban the unban time unix timestamp
     * @return human readable string on how long is left
     */
    public static String formatTimeLeft(long timeUnban){
        long duration = timeUnban - System.currentTimeMillis();
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        if(days > Short.MAX_VALUE){
            return " forever";
        }
        duration -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(mins);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        StringBuilder sb = new StringBuilder();
        if(days > 0L){
            sb.append(" ").append(days).append(days == 1 ? " day" : " days");
        }
        if(hours > 0L){
            sb.append(" ").append(hours).append(hours == 1 ? " hour" : " hours");
        }
        if(mins > 0L){
            sb.append(" ").append(mins).append(mins == 1 ? " min" : " mins");
        }
        if(seconds > 0L){
            sb.append(" ").append(seconds).append(seconds == 1 ? " second" : " seconds");
        }
        return sb.toString();
    }
}