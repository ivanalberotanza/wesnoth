/*******************************************************************************
 * Copyright (c) 2011 by Timotei Dolean <timotei21@gmail.com>
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.wesnoth.wml.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.wesnoth.utils.ResourceUtils;
import org.wesnoth.utils.WMLUtils;
import org.wesnoth.wml.WMLKey;
import org.wesnoth.wml.WMLMacroCall;
import org.wesnoth.wml.WMLRoot;
import org.wesnoth.wml.WMLTag;

import com.google.common.base.Preconditions;

/**
 * A simple WML Parser that parses a xtext WML Config file resource
 */
public class SimpleWMLParser
{
    protected WMLConfig config_;
    protected IFile file_;

    /**
     * Creates a new parser for the specified file
     */
    public SimpleWMLParser( IFile file )
    {
        file_ = file;
        config_ = new WMLConfig( file.getProjectRelativePath( ).toString( ) );
    }

    /**
     * Creates a new parser and fills the specified config file
     */
    public SimpleWMLParser( IFile file, WMLConfig config )
    {
        config_ = Preconditions.checkNotNull( config );
        file_ = file;
    }

    /**
     * Parses the config. The results will be available in {@link #getParsedConfig()}
     */
    public void parse()
    {
        WMLRoot root = ResourceUtils.getWMLRoot( file_ );
        TreeIterator<EObject> itor = root.eAllContents( );
        WMLTag currentTag = null;
        String currentTagName = "";

        while ( itor.hasNext( ) ) {
            EObject object = itor.next( );
            System.out.println( object );

            if ( object instanceof WMLTag ) {
                currentTag = ( WMLTag ) object;
                currentTagName = currentTag.getName( );

                if ( currentTagName.equals( "scenario" ) )
                    config_.IsScenario = true;
                else if ( currentTagName.equals( "campaign" ) )
                    config_.IsCampaign = true;
            }
            else if ( object instanceof WMLKey ) {
                if ( currentTag != null ) {
                    WMLKey key = ( WMLKey ) object;
                    String keyName = key.getName( );

                    if ( keyName.equals( "id" ) ) {
                        if ( currentTagName.equals( "scenario" ) )
                            config_.ScenarioId = WMLUtils.getKeyValue( key.getValue( ) );
                        else if ( currentTagName.equals( "campaign" ) )
                            config_.CampaignId = WMLUtils.getKeyValue( key.getValue( ) );
                    } else if ( keyName.equals( "name" ) ) {
                        if ( currentTagName.equals( "set_variable" ) ||
                             currentTagName.equals( "set_variables" ) ) {
                            handleSetVariable( object );
                        }
                    }
                }
            }
            else if ( object instanceof WMLMacroCall ) {
                WMLMacroCall macroCall = ( WMLMacroCall ) object;
                String macroCallName = macroCall.getName( );
                if ( macroCallName.equals( "VARIABLE" ) ) {
                    handleSetVariable( object );
                }
            }
        }

        System.out.println( "parsed config: " + config_ );
    }

    protected void handleSetVariable( EObject context )
    {

    }

    protected void handleUnsetVariable( EObject context )
    {

    }

    public WMLConfig getParsedConfig()
    {
        return config_;
    }
}
