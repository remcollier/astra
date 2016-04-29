/**
 * Title:       StringMessage.java
 * Copyright:   Copyright (c) 1996-2004 The Agent Factory Working Group. All rights reserved.
 * Licence:     This file is free software; you can redistribute it and/or modify
 *              it under the terms of the GNU Lesser General Public License as published by
 *              the Free Software Foundation; either version 2.1, or (at your option)
 *              any later version.
 *
 *              This file is distributed in the hope that it will be useful,
 *              but WITHOUT ANY WARRANTY; without even the implied warranty of
 *              MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *              GNU Lesser General Public License for more details.
 *
 *              You should have received a copy of the GNU Lesser General Public License
 *              along with Agent Factory; see the file COPYING.  If not, write to
 *              the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 *              Boston, MA 02111-1307, USA.
 */
package astra.messaging;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import astra.formula.Formula;

/**
 * Implementation of FIPA ACL Messaging Representation in String.
 * 
 * @author Rem Collier
 */
public class AstraMessage implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -850178269634558435L;
	
	public String performative;
    public String sender;
    public List<String> receivers= new LinkedList<String>();
    public String content;

    public String protocol;
    public String ontology;
    public String language = "astra"; // default value
    public String replyWith;
    public String inReplyTo;
    public String replyBy;
    public String replyTo;
    public String conversationId;
    public final String aclRepresentation = "fipa.acl.rep.string.std"; 
    public final String payloadEncoding = "US-ASCII";
    
    public String toString() {
    	return "sender: " + sender + " receivers: " + receivers + " performative: " + performative + " content: " + content;
    }
}
