package mork;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the part of a Mork file containing Group definitions. A group is
 * something like a transaction.
 * 
 * @author mhaller
 */
public class Groups {

    /** Internal container for all parsed groups */
    private List<Group> groups = new LinkedList<Group>();

    /**
     * Parse the given String for Group definitions
     * 
     * @param content
     *            the Mork content with Group definitions
     */
    public Groups(String content) {
        content = StringUtils.removeCommentLines(content);
        content = StringUtils.removeNewlines(content);
        Pattern pattern = 
            Pattern.compile("@\\$\\$\\{([0-9A-F]*)\\{@(.*)@\\$\\$\\}(\\1)\\}@");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String transactionId1 = matcher.group(1);
            String transactionContent = matcher.group(2);
            // String transactionId2 = matcher.group(3);
            groups.add(new Group(transactionId1, transactionContent));
        }
    }

    /**
     * Returns the number of groups found in the content
     * 
     * @return the number of groups
     */
    public int countGroups() {
        return groups.size();
    }

    /**
     * Return a Group by its position in the internal list.
     * 
     * @param i
     *            the index position (This is NOT the Group ID!)
     * @return the Group
     */
    public Group getGroup(int i) {
        return groups.get(i);
    }

}
