/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module org.apache.zookeeper.data {
    class Id {
        ustring scheme;
        ustring id;
    }
    class ACL {
        int perms;
        Id id;
    }
    // information shared with the client
    class Stat {
        long czxid;      // created zxid
        long mzxid;      // last modified zxid
        long ctime;      // created
        long mtime;      // last modified
        int version;     // version
        int cversion;    // child version
        int aversion;    // acl version
        long ephemeralOwner; // owner id if ephemeral, 0 otw
        int dataLength;  //length of the data in the node
        int numChildren; //number of children of this node
        long pzxid;      // last modified children
    }
    // information explicitly stored by the server persistently
    class StatPersisted {
        long czxid;      // created zxid
        long mzxid;      // last modified zxid
        long ctime;      // created
        long mtime;      // last modified
        int version;     // version
        int cversion;    // child version
        int aversion;    // acl version
        long ephemeralOwner; // owner id if ephemeral, 0 otw
        long pzxid;      // last modified children
    }

    class ClientInfo {
        ustring authScheme; // Authentication scheme
        ustring user;       // user name or any other id(for example ip)
    }
}