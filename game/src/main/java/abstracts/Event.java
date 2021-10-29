package abstracts;

import interfaces.Callback;

public abstract class Event {
	protected Entity interactor;
	protected Entity interactee;

	public Entity getInstigator() {
		return this.interactor;
	}

	public Entity getTarget() {
		return this.interactee;
	}

	public void setInstigator(Entity entity) {
		this.interactor = entity;
	}

	public void setTarget(Entity entity) {
		this.interactee = entity;
	}

	public abstract void trigger(Callback callback);
}
