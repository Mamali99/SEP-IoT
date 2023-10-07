import { html, LitElement, PropertyValueMap } from 'lit';
import { customElement, property, query } from 'lit/decorators.js';
import '@vaadin/grid/vaadin-grid-tree-column.js';
import '@vaadin/grid';
import type { GridDataProviderCallback, GridDataProviderParams } from '@vaadin/grid';
import { contextMenuRenderer } from '@vaadin/context-menu/lit.js';
import type { ContextMenuLitRenderer } from '@vaadin/context-menu/lit.js';
import type { Grid } from '@vaadin/grid';
import '@vaadin/context-menu';
import { ContextMenu } from '@vaadin/context-menu';

type Person = {
	firstName: string;
	manager: boolean;
	lastName: string;
	email: string;
	id: number;
	managerId: number | null;
}

type PeopleOptions = {
    managerId?: number | null;
    count?: number;
    startIndex?: number;
};

type PeopleResults = {
    people: Person[];
    hierarchyLevelSize: number;
};

var persons : Person[] = [];

export function getPeople(options?: PeopleOptions): PeopleResults {
	console.log(persons);
    let people = [...persons];

    if (options?.managerId !== undefined) {
        people = people.filter((person) => person.managerId === options?.managerId);
    }

    const hierarchyLevelSize = people.length;
    const startIndex = options?.startIndex ?? 0;
    const count = options?.count ? startIndex + options.count : undefined;

    people = people.slice(startIndex, count);
    people = people.map((person, index) => ({
        ...person,
        manager: persons.some((p) => p.managerId === person.id),
    }));

    return {
        people,
        hierarchyLevelSize,
    };
}

@customElement('tree-grid')
export class TreeGrid extends LitElement {
    @property()
    items: Person[] = [];

	connectedCallback(): void {
		super.connectedCallback();
    	persons = this.items;
    }	

    async dataProvider(
        params: GridDataProviderParams<Person>,
        callback: GridDataProviderCallback<Person>
    ) {
        // The requested page and the full length of the corresponding
        // hierarchy level is requested from the data service
        const { people, hierarchyLevelSize } = getPeople({
            count: params.pageSize,
            startIndex: params.page * params.pageSize,
            managerId: params.parentItem ? params.parentItem.id : null,
        });

        callback(people, hierarchyLevelSize);
    }	

    private renderMenu: ContextMenuLitRenderer = (context, menu) => {
        const { sourceEvent } = context.detail as { sourceEvent: Event };
        const grid = menu.firstElementChild as Grid<Person>;

        const eventContext = grid.getEventContext(sourceEvent);
        const person = eventContext.item!;

        const clickHandler = (action: string) => () => {
           const details : JSON = <JSON><unknown>{
	           "action": action,
            }
     	    const event = new CustomEvent('tree-event', {
		        detail: details,
                composed: true,
                cancelable: true,
                bubbles: true		
	        });
          	this.dispatchEvent(event);	
            //window.alert(`${action}: ${person.firstName} ${person.lastName}`);
        };

       return html`
           <vaadin-list-box>
             <vaadin-item @click="${clickHandler('Email')}">Email (${person.email})</vaadin-item>
             <vaadin-item @click="${clickHandler('Manager')}">Manager (${person.manager})</vaadin-item>
           </vaadin-list-box>
       `;
    };

    onContextMenu(e: MouseEvent) {
        // Prevent opening context menu on header row.
        if ((e.currentTarget as Grid).getEventContext(e).section !== 'body') {
           e.stopPropagation();
        }
    }

	render() {
		return html`
		    <vaadin-context-menu id="context-menu" ${contextMenuRenderer(this.renderMenu, [])}>
		   	   <vaadin-grid .dataProvider="${this.dataProvider}" @vaadin-contextmenu="${this.onContextMenu}">
                 <vaadin-grid-tree-column
                   path="firstName"
                   item-has-children-path="manager"
                 ></vaadin-grid-tree-column>
                 <vaadin-grid-column path="lastName"></vaadin-grid-column>
                 <vaadin-grid-column path="email"></vaadin-grid-column>			
			   </vaadin-grid>
			</vaadin-context-menu>
		`;
	}
}
