package com.jbirdvegas.mgerrit.objects;

/*
 * Copyright (C) 2013 Android Open Kang Project (AOKP)
 *  Author: Jon Stanford (JBirdVegas), 2013
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.jbirdvegas.mgerrit.helpers.Tools;

public class FileInfo {

    private String path;

    @SerializedName("old_path")
    private String oldPath;

    @SerializedName(JSONCommit.KEY_INSERTED)
    private int inserted = -1;

    @SerializedName(JSONCommit.KEY_DELETED)
    private int deleted = -1;

    @SerializedName(JSONCommit.KEY_STATUS)
    private Status status;

    @SerializedName("binary")
    private boolean isBinary = false;

    @SerializedName("isImage")
    private boolean isImage = false;

    // File status
    public enum Status {
        ADDED ("A"),
        DELETED("D"),
        RENAMED("R"),
        COPIED("C"),
        REWRITTEN("W"),
        MODIFIED("M");

        private final String statusCode;

        Status(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public static Status getValue(final String value) {
            if (value == null) return MODIFIED;
            for (Status s : values()) {
                if (value.equalsIgnoreCase(s.getStatusCode())) return s;
                else if (value.equalsIgnoreCase(s.name())) return s;
            }

            return MODIFIED;
        };
    }

    public FileInfo(String draft) {
        path = draft;
    }

    public String getPath() {
        return this.path;
    }

    public String getOldPath() { return oldPath; }

    public int getInserted() {
        return this.inserted;
    }

    public int getDeleted() {
        return this.deleted;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public boolean isBinary() { return isBinary; }

    public boolean isImage() { return isImage; }

    public static FileInfo deserialise(JsonObject object, String _path) {
        FileInfo file = new Gson().fromJson(object, FileInfo.class);
        file.path = _path;

        // Set the status
        String statusValue = "";
        if (object.has(JSONCommit.KEY_STATUS)) {
            statusValue = object.get(JSONCommit.KEY_STATUS).getAsString();
        }
        file.status = Status.getValue(statusValue);

        file.isImage = (file.isBinary() && Tools.isImage(file.path));
        return file;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "path='" + path + '\'' +
                ", oldPath='" + oldPath + '\'' +
                ", inserted=" + inserted +
                ", deleted=" + deleted +
                ", status=" + status +
                ", isBinary=" + isBinary +
                ", isImage=" + isImage +
                '}';
    }
}