package com.sensorwave.iotprocessor.clients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReverseGeocodingResult  {

    private List<ReverseGeocodingData> data = null;

    /**
    * Get data
    * @return data
    **/
    @JsonProperty("data")
    public List<ReverseGeocodingData> getData() {
        return data;
    }

    /**
     * Set data
     **/
    public void setData(List<ReverseGeocodingData> data) {
        this.data = data;
    }

    public ReverseGeocodingResult data(List<ReverseGeocodingData> data) {
        this.data = data;
        return this;
    }
    public ReverseGeocodingResult addDataItem(ReverseGeocodingData dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReverseGeocodingResult {\n");

        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReverseGeocodingResultQueryParam  {

        @jakarta.ws.rs.QueryParam("data")
        private List<ReverseGeocodingData> data = null;

        /**
        * Get data
        * @return data
        **/
        @JsonProperty("data")
        public List<ReverseGeocodingData> getData() {
            return data;
        }

        /**
         * Set data
         **/
        public void setData(List<ReverseGeocodingData> data) {
            this.data = data;
        }

        public ReverseGeocodingResultQueryParam data(List<ReverseGeocodingData> data) {
            this.data = data;
            return this;
        }
        public ReverseGeocodingResultQueryParam addDataItem(ReverseGeocodingData dataItem) {
            this.data.add(dataItem);
            return this;
        }

        /**
         * Create a string representation of this pojo.
         **/
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("class ReverseGeocodingResultQueryParam {\n");

            sb.append("    data: ").append(toIndentedString(data)).append("\n");
            sb.append("}");
            return sb.toString();
        }

        /**
         * Convert the given object to string with each line indented by 4 spaces
         * (except the first line).
         */
        private static String toIndentedString(Object o) {
            if (o == null) {
                return "null";
            }
            return o.toString().replace("\n", "\n    ");
        }
    }
}