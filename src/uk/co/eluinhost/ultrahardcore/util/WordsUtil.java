package uk.co.eluinhost.ultrahardcore.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.plugin.java.JavaPlugin;

//TODO use a config file instead
public class WordsUtil extends JavaPlugin {

    private static final String[] ADJECTIVES = {
            "Fast",
            "Quick",
            "Speedy",
            "Swift",
            "Hasty",
            "Zippy",
            "Rapid",
            "Slow",
            "Sluggish",
            "Creeping",
            "Dawdling",
            "Meandering",
            "Crawling",
            "Beautiful",
            "Striking",
            "Stunning",
            "Gorgeous",
            "Picturesque",
            "Lovely",
            "Charming",
            "Enchanting",
            "Exquisite",
            "Delicate",
            "Ugly",
            "Hideous",
            "Horrid",
            "Dreadful",
            "Obnoxious",
            "Nasty",
            "Ghastly",
            "Cruel",
            "Revolting",
            "Intimidating",
            "Menacing",
            "Miserable",
            "Dangerous",
            "Rude",
            "Spoiled",
            "Wild",
            "Lazy",
            "Selfish",
            "Delinquent",
            "Greedy",
            "Vile",
            "Ridiculous",
            "Kind",
            "Gentle",
            "Quiet",
            "Caring",
            "Fair",
            "Compassionate",
            "Benevolent",
            "Polite",
            "Amusing",
            "Generous",
            "Entertaining",
            "Hopeful",
            "Lively",
            "Creative",
            "Brave",
            "Good",
            "Fantastic",
            "Marvelous",
            "Fabulous",
            "Splendid",
            "Brilliant",
            "Superb",
            "Dynamite",
            "Bad",
            "Dreadful",
            "Terrible",
            "Ghastly",
            "Filthy",
            "Repulsive",
            "Awful",
            "Happy",
            "Joyful",
            "Ecstatic",
            "Cheerful",
            "Delighted",
            "Blithe",
            "Carefree",
            "Bored",
            "Hardworking",
            "Mysterious",
            "Verbose",
            "Laconic",
            "Curious",
            "Bucolic",
            "Silly",
            "Contrary",
            "Shocking",
            "Wild",
            "Rambunctious",
            "Courageous",
            "Cowardly",
            "Ornery",
            "Gullible",
            "Thrifty",
            "Famous",
            "Infamous",
            "Brazen",
            "Cold",
            "Hard",
            "Subtle",
            "Gullible",
            "Hungry",
            "Anxious",
            "Nervous",
            "Antsy",
            "Impatient",
            "Shining",
            "Crispy",
            "Soaring",
            "Endless",
            "Sparkling",
            "Fluttering",
            "Spiky",
            "Scrumptious",
            "Eternal",
            "Slimy",
            "Slick",
            "Gilded",
            "Ancient",
            "Smelly",
            "Glowing",
            "Rotten",
            "Decrepit",
            "Lousy",
            "Grimy",
            "Rusty",
            "Sloppy",
            "Muffled",
            "Foul",
            "Rancid",
            "Fetid",
            "Small",
            "Itty-bitty",
            "Tiny",
            "Puny",
            "Miniscule",
            "Minute",
            "Diminutive",
            "Petite",
            "Slight",
            "Big",
            "Huge",
            "Gigantic",
            "Monstrous",
            "Immense",
            "Great",
            "Tremendous",
            "Enormous",
            "Massive",
            "Whopping",
            "Vast",
            "Brawny",
            "Hulking",
            "Bulky",
            "Towering",
            "Hot",
            "Steaming",
            "Sweltering",
            "Scorching",
            "Blistering",
            "Sizzling",
            "Muggy",
            "Stifling",
            "Sultry",
            "Oppressive",
            "Cold",
            "Chilly",
            "Freezing",
            "Icy",
            "Frosty",
            "Bitter",
            "Arctic",
            "Difficult",
            "Demanding",
            "Trying",
            "Challenging",
            "Easy",
            "Simple",
            "Effortless",
            "Relaxed",
            "Calm",
            "Tranquil",
            "Heavy",
            "Serious",
            "Grave",
            "Profound",
            "Intense",
            "Severe"
    };

    private static final String[] NOUNS = {
            "Alumni",
            "Analysers",
            "Aquarist",
            "Archnemeses",
            "Atlasians",
            "Axe Dogers",
            "Babies",
            "Badgers",
            "Beach Whales",
            "Calves",
            "Children",
            "Churchgoers",
            "Circus Freaks",
            "Citydwellers",
            "COD Fanboys",
            "Copyists",
            "Dominos",
            "Dwarves",
            "Echos",
            "Elves",
            "Family",
            "Fliers",
            "Fungii",
            "Heros",
            "Hippopotamii",
            "Hoaxes",
            "Hooves",
            "Kisses",
            "Ladies",
            "Lives",
            "Men",
            "Messes",
            "Moose",
            "Mice",
            "Nannies",
            "Octopii",
            "Party",
            "People",
            "Potatoes",
            "Runners-up",
            "Scratchers",
            "Sheep",
            "Species",
            "Splashers",
            "Spies",
            "Stitchers",
            "Stories",
            "Thieves",
            "Waltzers",
            "Watchers",
            "Wives",
            "Women"};

    private static final Random RANDOM = new Random();

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
     * @return a random team name
     */
    public static String getRandomTeamName() {
        return "The " + ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)] + " " + NOUNS[RANDOM.nextInt(NOUNS.length)];
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
}