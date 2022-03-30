package net.sattler22.transfer.domain;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Image Business Object
 *
 * @author Pete Sattler
 * @version August 2019
 */
public record Image(@JsonProperty("src") URI source, @JsonProperty("alt") String altText) {
}
