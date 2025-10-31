package net.sattler22.transfer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

/**
 * Image Business Object
 *
 * @author Pete Sattler
 * @version August 2019
 */
public record Image(@JsonProperty("src") URI source, @JsonProperty("alt") String altText) {
}
