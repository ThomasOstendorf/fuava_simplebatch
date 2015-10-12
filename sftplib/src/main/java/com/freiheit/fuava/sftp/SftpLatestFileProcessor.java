/*
 * (c) Copyright 2015 freiheit.com technologies GmbH
 *
 * Created on 09.10.15 by thomas.ostendorf@freiheit.com
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */

package com.freiheit.fuava.sftp;

import com.freiheit.fuava.simplebatch.fetch.FetchedItem;
import com.freiheit.fuava.simplebatch.processor.AbstractSingleItemProcessor;
import com.freiheit.fuava.simplebatch.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Processor for the file one wants to download.
 *
 * @author Thomas Ostendorf (thomas.ostendorf@freiheit.com)
 */
public class SftpLatestFileProcessor extends
        AbstractSingleItemProcessor<FetchedItem<SftpFilename>, SftpFilename, InputStream> {

    private static final Logger LOG = LoggerFactory.getLogger( SftpLatestFileProcessor.class );

    private final RemoteClient client;
    private final String archivedFolder;

    /**
     *
     * @param client
     *            SFTP client.
     * @param archivedFolder
     *            Processed (downloaded) files are moved to this folder on
     *            remote server.
     */
    public SftpLatestFileProcessor( final RemoteClient client, final String archivedFolder ) {
        this.client = client;
        this.archivedFolder = archivedFolder;
    }

    public Result<FetchedItem<SftpFilename>, InputStream> processItem( final Result<FetchedItem<SftpFilename>, SftpFilename> data ) {
        if ( data.isFailed() ) {
            return Result.<FetchedItem<SftpFilename>, InputStream> builder( data ).failed();
        } else {
            try {
                final InputStream inputStream = client.downloadRemoteFile( data.getOutput().getRemoteFullPath() );

                return Result.success( data.getInput(), inputStream );
            } catch ( final Throwable e ) {
                return Result.failed( data.getInput(), e );
            }
        }
    }




}