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

package org.apache.cassandra.db;

import java.util.*;
import java.io.*;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.io.util.FileUtils;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;

public class RecoveryManager
{
    private static Logger logger_ = Logger.getLogger(RecoveryManager.class);

    public static File[] getListofCommitLogs()
    {
        String directory = DatabaseDescriptor.getLogFileLocation();
        File file = new File(directory);
        return file.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                // throw out anything that starts with dot.
                return !name.matches("\\..*");
            }
        });
    }

    public static void doRecovery() throws IOException
    {
        File[] files = getListofCommitLogs();
        if (files.length == 0)
            return;

        Arrays.sort(files, new FileUtils.FileComparator());
        logger_.info("Replaying " + StringUtils.join(files, ", "));
        CommitLog.open().recover(files);
        FileUtils.delete(files);
        logger_.info("Log replay complete");
    }
}
