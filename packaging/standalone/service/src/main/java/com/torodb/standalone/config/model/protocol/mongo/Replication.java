/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.standalone.config.model.protocol.mongo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.torodb.packaging.config.annotation.Description;
import com.torodb.packaging.config.model.protocol.mongo.AbstractReplication;
import com.torodb.packaging.config.model.protocol.mongo.Auth;
import com.torodb.packaging.config.model.protocol.mongo.FilterList;
import com.torodb.packaging.config.model.protocol.mongo.Role;
import com.torodb.packaging.config.model.protocol.mongo.SSL;
import com.torodb.packaging.config.validation.RequiredParametersForAuthentication;

@JsonPropertyOrder({
    "replSetName",
    "syncSource",
	"role",
	"ssl",
	"auth",
	"include",
	"exclude"
})
public class Replication extends AbstractReplication {
	
    @Description("config.mongo.replication.replSetName")
    @NotEmpty
    @JsonProperty(required=true)
	public String getReplSetName() {
		return super.getReplSetName();
	}
    
    @Description("config.mongo.replication.role")
    @NotNull
    @JsonProperty(required=true)
	public Role getRole() {
		return super.getRole();
	}
    
    @Description("config.mongo.replication.syncSource")
    @NotNull
    @JsonProperty(required=true)
	public String getSyncSource() {
		return super.getSyncSource();
	}
    
    @Description("config.mongo.replication.ssl")
    @NotNull
    @JsonProperty(required=true)
    public SSL getSsl() {
        return super.getSsl();
    }
    
    @Description("config.mongo.replication.auth")
    @NotNull
    @JsonProperty(required=true)
    @RequiredParametersForAuthentication
    public Auth getAuth() {
        return super.getAuth();
    }
    
    @Description("config.mongo.replication.include")
    @JsonProperty(required=true)
    public FilterList getInclude() {
        return super.getInclude();
    }
    
    @Description("config.mongo.replication.exclude")
    @JsonProperty(required=true)
    public FilterList getExclude() {
        return super.getExclude();
    }
    
}
