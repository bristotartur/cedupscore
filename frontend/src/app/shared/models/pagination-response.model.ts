import { PageDetails } from "./page-details.model";

export interface PaginationResponse<T> {
    content: T[],
    page: PageDetails
}