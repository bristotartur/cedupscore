export interface ExceptionResponse {
    title: string,
    status: number,
    details: string,
    developerMessage: string,
    timestamp: string,
    fields?: string,
    fieldsMessages?: string
}