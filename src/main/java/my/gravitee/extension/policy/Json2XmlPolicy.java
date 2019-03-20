/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package my.gravitee.extension.policy;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.stream.BufferedReadWriteStream;
import io.gravitee.gateway.api.stream.ReadWriteStream;
import io.gravitee.gateway.api.stream.SimpleReadWriteStream;
import io.gravitee.policy.api.PolicyChain;
import io.gravitee.policy.api.PolicyResult;
import io.gravitee.policy.api.annotations.OnRequest;
import io.gravitee.policy.api.annotations.OnRequestContent;
import io.gravitee.policy.api.annotations.OnResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.XML;


@SuppressWarnings("unused")
public class Json2XmlPolicy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Json2XmlPolicy.class);

    /**
     * The associated configuration to this Json2Xml Policy
     */
    private Json2XmlPolicyConfiguration configuration;

    /**
     * Create a new Json2Xml Policy instance based on its associated configuration
     *
     * @param configuration the associated configuration to the new Json2Xml Policy instance
     */
    public Json2XmlPolicy(Json2XmlPolicyConfiguration configuration) {
        this.configuration = configuration;
    }

    @OnRequestContent
    public ReadWriteStream onRequestContent(Request request, Response response, ExecutionContext executionContext, PolicyChain policyChain) {
        Json2XmlPolicy instance = this;
        
        return new BufferedReadWriteStream() {
            private Buffer buffer;

            @Override
            public SimpleReadWriteStream<Buffer> write(Buffer content) {
                if (buffer == null) {
                    buffer = Buffer.buffer();
                }

                buffer.appendBuffer(content);
                return this;
            }

            @Override
            public void end() {
                if (buffer != null)
                {
                    String body = buffer.toString();
                    body = Json2XmlPolicy.jsonToXml(body);
                    super.write(Buffer.buffer(body));
                }
                super.end();
            }
        };


    }

    private static String jsonToXml(String json_value) {
        String xml = "";
        try {
            org.json.JSONObject jsoObject = new org.json.JSONObject(json_value);
            xml = xml + XML.toString(jsoObject);
        } catch (Exception e) {
            System.out.println(e);
        }
        xml = xml + "";
        return xml;
    }

}
