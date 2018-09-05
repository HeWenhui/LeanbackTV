/*
 *  Copyright 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import java.util.LinkedList;
import java.util.List;

/**
 * Description of media constraints for {@code MediaStream} and
 * {@code PeerConnection}.
 */
public class MediaConstraints {
    /** Simple String key/value pair. */
    public static class KeyValuePair {
        private final String key;
        private final String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return key + ": " + value;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            KeyValuePair that = (KeyValuePair) other;
            return key.equals(that.key) && value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return key.hashCode() + value.hashCode();
        }
    }

    public final List<KeyValuePair> mandatory;
    public final List<KeyValuePair> optional;

    public MediaConstraints() {
        mandatory = new LinkedList<KeyValuePair>();
        optional = new LinkedList<KeyValuePair>();
    }

    private static String stringifyKeyValuePairList(List<KeyValuePair> list) {
        StringBuilder builder = new StringBuilder("[");
        for (KeyValuePair pair : list) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(pair.toString());
        }
        return builder.append("]").toString();
    }

    public String toString() {
        return "mandatory: " + stringifyKeyValuePairList(mandatory) + ", optional: "
                + stringifyKeyValuePairList(optional);
    }
}
