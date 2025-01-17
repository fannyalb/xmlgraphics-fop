/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.render.pdf;

import org.apache.fop.events.EventBroadcaster;
import org.apache.fop.events.EventProducer;

/**
 * Event producer interface for events generated by the PDF renderer.
 * PDFEventProducer.xml should include a message for all event-raising methods.
 */
public interface PDFEventProducer extends EventProducer {

    /** Provider class for the event producer. */
    final class Provider {

        /**
         * Utility classes should not have a public or default constructor.
         */
        private Provider() { }

        /**
         * Returns an event producer.
         * @param broadcaster the event broadcaster to use
         * @return the event producer
         */
        public static PDFEventProducer get(EventBroadcaster broadcaster) {
            return broadcaster.getEventProducerFor(PDFEventProducer.class);
        }
    }

    /**
     * Some link targets haven't been fully resolved.
     * @param source the event source
     * @param count the number of unresolved links
     * @event.severity WARN
     */
    void nonFullyResolvedLinkTargets(Object source, int count);


    /**
     * Custom structure type is not standard as per the PDF reference.
     *
     * @param source the event source
     * @param type custom structure type
     * @param fallback default structure type used as a fallback
     * @event.severity WARN
     */
    void nonStandardStructureType(Object source, String type, String fallback);

    /**
     * The encryption length must be a multiple of 8 between 40 and 128.
     *
     * @param source the event source
     * @param originalValue requested encryption length
     * @param correctedValue corrected encryption length
     * @event.severity WARN
     */
    void incorrectEncryptionLength(Object source, int originalValue, int correctedValue);

    /**
     * The language of a piece of text is unknown.
     *
     * @param source the event source
     * @param location location in the source FO file, if any
     */
    void unknownLanguage(Object source, String location);

    /**
     * Unicode char map ended with an unpaired surrogate.
     *
     * @param source the event source
     * @event.severity ERROR
     */
    void unpairedSurrogate(Object source);
}
