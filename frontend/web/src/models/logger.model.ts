export interface LoggerModel {
    id: number;
    name: string;
    description: string;
    password?: string | null;
    createdAt: number;
}

export interface CreateLoggerRequest {
    name: string;
    description: string;
}

export interface UpdateLoggerRequest {
    id: number;
    name?: string | null;
    description?: string | null;
}