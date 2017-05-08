/*
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.batch.item.support.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Glenn Renfro
 */
public class CompositeItemWriterBuilderTests {

	@Test
	public void testProcess() throws Exception {

		final int NUMBER_OF_WRITERS = 10;
		List<Object> data = Collections.singletonList(new Object());

		List<ItemWriter<? super Object>> writers = new ArrayList<ItemWriter<? super Object>>();

		for (int i = 0; i < NUMBER_OF_WRITERS; i++) {
			ItemWriter<? super Object> writer = mock(ItemWriter.class);
			writers.add(writer);
		}
		CompositeItemWriter itemWriter = new CompositeItemWriterBuilder().delegates(writers).build();
		itemWriter.setDelegates(writers);
		itemWriter.write(data);

		for (ItemWriter<? super Object> writer : writers) {
			verify(writer).write(data);
		}

	}

	@Test
	public void isStreamOpen() throws Exception {
		ignoreItemStream(false);
		ignoreItemStream(true);
	}

	private void ignoreItemStream(boolean ignoreItemStream) throws Exception {
		ItemStreamWriter<? super Object> writer = mock(ItemStreamWriter.class);
		List<Object> data = Collections.singletonList(new Object());
		ExecutionContext executionContext = new ExecutionContext();

		List<ItemWriter<? super Object>> writers = new ArrayList<ItemWriter<? super Object>>();
		writers.add(writer);
		CompositeItemWriter itemWriter = new CompositeItemWriterBuilder().delegates(writers)
				.ignoreItemStream(ignoreItemStream).build();
		itemWriter.open(executionContext);

		int openCount = 0;
		if (!ignoreItemStream) {
			openCount = 1;
		}
		// If user has set ignoreItemStream to true, then it is expected that they opened the delegate writer.
		verify(writer, times(openCount)).open(executionContext);
		itemWriter.write(data);
	}

}
