package abstracts;

import interfaces.Callback;

public abstract class Event {
	protected Entity interactor;
	protected Entity interactee;

	public abstract void trigger(Callback callback);

	public void setInstigator(Entity entity) {
		this.interactor = entity;
	}

	public Entity getInstigator() {
		return this.interactor;
	}

	public void setTarget(Entity entity) {
		this.interactee = entity;
	}

	public Entity getTarget() {
		return this.interactee;
	}
}
