/*
 * Copyright 2018~2020 java-diff-utils.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.idex.diff

/**
 * Specifies the type of the delta.
 *
 */
enum class DeltaType {
	/**
	 * A change in the original.
	 */
	change,
	
	/**
	 * A delete from the original.
	 */
	delete,
	
	/**
	 * An insert into the original.
	 */
	insert,
	
	/**
	 * An do nothing.
	 */
	equal
}
