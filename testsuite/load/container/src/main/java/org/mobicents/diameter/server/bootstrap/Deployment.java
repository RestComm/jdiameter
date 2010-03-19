/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.diameter.server.bootstrap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
/**
 *
 * @author kulikov
 */
public class Deployment {

    private File file;
    private boolean isDirectory;
    private long lastModified;
    
    public Deployment(File file) {
        this.file = file;
        this.isDirectory = file.isDirectory();
        this.lastModified = file.lastModified();
    }
    
    public boolean isDirectory() {
        return this.isDirectory;
    }
    
    public URL getURL() {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    public long lastModified() {
        return lastModified;
    }
    
    public void update(long lastModified) {
        this.lastModified = lastModified;
    }
}
