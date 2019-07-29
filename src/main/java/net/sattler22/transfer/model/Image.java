package net.sattler22.transfer.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.jcip.annotations.Immutable;

/**
 * Image Business Object
 *
 * @author Pete Sattler
 * @version July 2019
 */
@Immutable
public final class Image implements Serializable {

    private static final long serialVersionUID = -2240792763050541980L;
    private final URI source;
    private final String altText;

    /**
     * Constructs a new image
     *
     * @param source The source URI
     * @param altText The alternative text
     */
    public Image(@JsonProperty("src") URI source,
                 @JsonProperty("alt") String altText) {
        this.source = source;
        this.altText = altText;
    }

    @JsonProperty("src")
    public URI getSource() {
        return source;
    }

    @JsonProperty("alt")
    public String getAltText() {
        return altText;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        final Image that = (Image) other;
        return Objects.equals(this.source, that.source);
    }

    @Override
    public String toString() {
        return String.format("%s [source=%s, altText=%s]",
                             getClass().getSimpleName(), source, altText);
    }
}
