
package com.didispace.web;

///实现ListenableFutureCallback，实现回调功能
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class CommonListenableCallBack<T> implements ListenableFutureCallback<T> {
	private Map<String, Object> resultValue;
	private CountDownLatch latch;

	public CommonListenableCallBack(Map<String, Object> resultValue, CountDownLatch latch) {

		this.resultValue = resultValue;
		this.latch = latch;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(T result) {
		ResponseEntity<T> re = (ResponseEntity<T>) result;
		if (re != null && re.getBody() != null) {
			T body = re.getBody();
			resultValue.put("resbody", body);

		}
		latch.countDown();
	}

	@Override
	public void onFailure(Throwable ex) {
		resultValue.put("resbody", null);
		latch.countDown();
	}

}