package seedu.mark.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.mark.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.mark.commons.core.index.Index;
import seedu.mark.commons.util.StringUtil;
import seedu.mark.logic.commands.TabCommand;
import seedu.mark.logic.commands.TabCommand.Tab;
import seedu.mark.logic.parser.exceptions.ParseException;
import seedu.mark.model.bookmark.Folder;
import seedu.mark.model.bookmark.Name;
import seedu.mark.model.bookmark.Remark;
import seedu.mark.model.bookmark.Url;
import seedu.mark.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String folder} into a {@code Folder}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code folder} is invalid.
     */
    public static Folder parseFolder(String folder) throws ParseException {
        requireNonNull(folder);
        String trimmedFolder = folder.trim();
        if (!Folder.isValidFolder(trimmedFolder)) {
            throw new ParseException(Folder.MESSAGE_CONSTRAINTS);
        }
        return new Folder(trimmedFolder);
    }

    /**
     * Parses a {@code String remark} into an {@code Remark}.
     * Leading and trailing whitespaces will be trimmed.
     * Empty remarks will be replaced by the default {@code Remark}.
     *
     * @throws ParseException if the given {@code remark} is invalid.
     */
    public static Remark parseRemark(String remark) throws ParseException {
        requireNonNull(remark);
        String trimmedRemark = remark.trim();

        if (Remark.isEmptyRemark(trimmedRemark)) {
            return Remark.getDefaultRemark();
        } else if (!Remark.isValidRemark(trimmedRemark)) {
            throw new ParseException(Remark.MESSAGE_CONSTRAINTS);
        }

        return new Remark(trimmedRemark);
    }

    /**
     * Parses a {@code String url} into a {@code Url}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code url} is invalid.
     */
    public static Url parseUrl(String url) throws ParseException {
        requireNonNull(url);
        String trimmedUrl = url.trim();
        if (!Url.isValidUrl(trimmedUrl)) {
            throw new ParseException(Url.MESSAGE_CONSTRAINTS);
        }
        return new Url(trimmedUrl);
    }

    /**
     * Parses a {@code String args} into a {@code Tab}.
     * {@code args} is valid if it is either 1, 2 or 3, or the keywords of tab.
     * @param args A valid argument of Tab
     * @return The corresponding tab
     * @throws ParseException if the given {@code arg} is invalid.
     */
    public static Tab parseTab(String args) throws ParseException {

        Tab tab;
        try {
            tab = ParserUtil.parseTabIndex(args);
        } catch (ParseException pe_index) {

            try {
                tab = ParserUtil.parseTabKeyword(args);
            } catch (ParseException pe_kw) {
                throw pe_index;
            }

        }

        return tab;
    }

    /**
     * Parses a {@code String arg} into a {@code Tab}.
     * Parsing will be successful only if {@code arg} is either "1", "2" or "3".
     *
     * @param arg The argument of a tab command
     * @return The corresponding tab
     * @throws ParseException if the given {@code arg} is invalid.
     */
    public static Tab parseTabIndex(String arg) throws ParseException {
        int index;
        try {
            index = Integer.parseInt(arg.strip());
        } catch (NumberFormatException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, TabCommand.MESSAGE_USAGE));
        }

        return convertIndexToTabType(index);
    }

    /**
     * Converts an {@code Index} into a {@code Tab}.
     * @param index A one-based index
     * @return The corresponding tab
     * @throws ParseException if index does not represent 1, 2 or 3.
     */
    private static Tab convertIndexToTabType(int index) throws ParseException {

        Tab type = null;
        switch (index) {
        case 1:
            type = Tab.DASHBOARD;
            break;
        case 2:
            type = Tab.ONLINE;
            break;
        case 3:
            type = Tab.OFFLINE;
            break;
        default:
            throw new ParseException(TabCommand.MESSAGE_INVALID_INDEX);
        }

        return type;
    }

    /**
     * Parses a {@code String arg} into a {@code Tab}.
     * Parsing will be successful only if {@code arg} is either "dash", "on" or "off".
     *
     * @param arg The argument of a tab command
     * @return The corresponding tab
     * @throws ParseException if the given {@code arg} is invalid.
     */
    public static Tab parseTabKeyword(String arg) throws ParseException {
        Tab type = null;

        switch (arg.toLowerCase().strip()) {
        case "dash":
            type = Tab.DASHBOARD;
            break;
        case "on":
            type = Tab.ONLINE;
            break;
        case "off":
            type = Tab.OFFLINE;
            break;
        default:
            throw new ParseException(TabCommand.MESSAGE_INVALID_KEYWORD);
        }

        return type;
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
