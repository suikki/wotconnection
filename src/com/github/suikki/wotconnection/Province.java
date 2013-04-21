/*
 * Copyright (C) 2013 Olli Kallioinen
 * http://suikki.wordpress.com/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.suikki.wotconnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing one WOT province.
 * 
 * @author Olli Kallioinen
 */
public class Province {

	private final String id;
	private final String name;

	Province(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the id of the province.
	 * 
	 * @return the id of the province.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the name of the province.
	 * 
	 * @return the name of the province.
	 */
	public String getName() {
		return name;
	}

	static Province readFromJson(JSONObject jsonObject) throws JSONException {
		String id = jsonObject.getString("id");
		String name = jsonObject.getString("name");
		return new Province(id, name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Province other = (Province) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

}
