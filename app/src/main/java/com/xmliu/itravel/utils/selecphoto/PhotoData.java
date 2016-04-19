/*************************************************************************
 *  
 *  Copyright (C) 2013 SuZhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel.utils.selecphoto;

/**
 * @date: 2013-11-7 上午9:05:23
 * 
 * @email: tchen@raipeng.com
 * 
 * @version: V1.0
 * 
 * @description:
 * 
 */
public class PhotoData {

	private int id;
	private int cloudalbumId;
	private String image;
	private int localalbumId;
	private String path;
	private int role;
private String description;

	public String getDesction() {
	return description;
}

public void setDesction(String desction) {
	this.description = desction;
}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCloudalbumId() {
		return cloudalbumId;
	}

	public void setCloudalbumId(int cloudalbumId) {
		this.cloudalbumId = cloudalbumId;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getLocalalbumId() {
		return localalbumId;
	}

	public void setLocalalbumId(int localalbumId) {
		this.localalbumId = localalbumId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

}
