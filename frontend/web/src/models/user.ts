export interface AuthenticationDetails {
    userId: number;
    email: string;
    displayName: string;
}

export interface UpdateSettingsRequest {
    displayName: string;
}